from __future__ import annotations

import argparse
import cgi
import json
import mimetypes
import os
import uuid
from http import HTTPStatus
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import unquote, urlparse
from urllib import error, request

BACKEND_MAP = [
    ("/api/adopters", "http://127.0.0.1:8083"),
    ("/api/shelters", "http://127.0.0.1:8082"),
    ("/api/pets", "http://127.0.0.1:8081"),
    ("/api/matches", "http://127.0.0.1:8086"),
    ("/api/notifications", "http://127.0.0.1:8084"),
    ("/veterinarians", "http://127.0.0.1:8085"),
    ("/vaccination-cards", "http://127.0.0.1:8085"),
    ("/vaccines", "http://127.0.0.1:8085"),
    ("/medical-events", "http://127.0.0.1:8085"),
    ("/appointments", "http://127.0.0.1:8085"),
]

APP_ROOT = Path(__file__).resolve().parent
UPLOAD_DIR = APP_ROOT / "uploads"
MAX_UPLOAD_BYTES = 10 * 1024 * 1024
ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"}


class PetFinderHandler(SimpleHTTPRequestHandler):
    def __init__(self, *args, directory: str | None = None, **kwargs):
        self.upload_dir = UPLOAD_DIR
        self.upload_dir.mkdir(parents=True, exist_ok=True)
        super().__init__(*args, directory=directory, **kwargs)

    def end_headers(self):
        # Helpful even if the frontend is served from this proxy, and harmless for API responses.
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requester-Role")
        self.send_header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
        self.send_header("Pragma", "no-cache")
        self.send_header("Expires", "0")
        super().end_headers()

    def do_OPTIONS(self):
        target = self._resolve_backend(self.path)
        if target:
            self._proxy(target, include_body=False)
            return
        self.send_response(HTTPStatus.NO_CONTENT)
        self.end_headers()

    def do_GET(self):
        parsed_path = urlparse(self.path).path
        if parsed_path == "/uploads" or parsed_path == "/uploads/" or parsed_path.startswith("/uploads/"):
            self._serve_upload_file()
            return
        target = self._resolve_backend(self.path)
        if target:
            self._proxy(target)
            return
        super().do_GET()

    def do_POST(self):
        parsed_path = urlparse(self.path).path
        if parsed_path == "/uploads" or parsed_path == "/uploads/" or parsed_path.startswith("/uploads/"):
            self._handle_upload()
            return
        target = self._resolve_backend(self.path)
        if target:
            self._proxy(target)
            return
        self.send_error(HTTPStatus.NOT_FOUND, "Not found")

    def do_PUT(self):
        target = self._resolve_backend(self.path)
        if target:
            self._proxy(target)
            return
        self.send_error(HTTPStatus.NOT_FOUND, "Not found")

    def do_PATCH(self):
        target = self._resolve_backend(self.path)
        if target:
            self._proxy(target)
            return
        self.send_error(HTTPStatus.NOT_FOUND, "Not found")

    def do_DELETE(self):
        target = self._resolve_backend(self.path)
        if target:
            self._proxy(target)
            return
        self.send_error(HTTPStatus.NOT_FOUND, "Not found")

    def _resolve_backend(self, path: str):
        for prefix, base in BACKEND_MAP:
            if path == prefix or path.startswith(prefix + "/") or path.startswith(prefix + "?"):
                return base, path
        return None

    def _is_upload_path(self, path: str) -> bool:
        parsed = urlparse(path)
        return parsed.path == "/uploads" or parsed.path.startswith("/uploads/")

    def _serve_upload_file(self):
        parsed = urlparse(self.path)
        relative = parsed.path.removeprefix("/uploads/")
        if not relative or relative == "/uploads":
            self.send_error(HTTPStatus.NOT_FOUND, "File not found")
            return

        safe_name = Path(unquote(relative)).name
        file_path = self.upload_dir / safe_name
        if not file_path.exists() or not file_path.is_file():
            self.send_error(HTTPStatus.NOT_FOUND, "File not found")
            return

        payload = file_path.read_bytes()
        content_type, _ = mimetypes.guess_type(str(file_path))
        self.send_response(HTTPStatus.OK)
        self.send_header("Content-Type", content_type or "application/octet-stream")
        self.send_header("Content-Length", str(len(payload)))
        self.end_headers()
        self.wfile.write(payload)

    def _handle_upload(self):
        content_type = self.headers.get("Content-Type", "")
        content_length = int(self.headers.get("Content-Length", "0") or "0")

        if "multipart/form-data" not in content_type:
            self._json_response(HTTPStatus.BAD_REQUEST, {"error": "Expected multipart/form-data"})
            return

        if content_length <= 0:
            self._json_response(HTTPStatus.BAD_REQUEST, {"error": "Empty upload"})
            return

        if content_length > MAX_UPLOAD_BYTES:
            self._json_response(HTTPStatus.REQUEST_ENTITY_TOO_LARGE, {"error": "File too large"})
            return

        form = cgi.FieldStorage(
            fp=self.rfile,
            headers=self.headers,
            environ={
                "REQUEST_METHOD": "POST",
                "CONTENT_TYPE": content_type,
                "CONTENT_LENGTH": str(content_length),
            },
            keep_blank_values=False,
        )

        upload_field = form["image"] if "image" in form else None
        if upload_field is None or getattr(upload_field, "file", None) is None:
            self._json_response(HTTPStatus.BAD_REQUEST, {"error": "Field 'image' is required"})
            return

        source_name = Path(upload_field.filename or "image").name
        extension = Path(source_name).suffix.lower()
        if extension not in ALLOWED_EXTENSIONS:
            self._json_response(HTTPStatus.BAD_REQUEST, {"error": "Unsupported file type"})
            return

        data = upload_field.file.read(MAX_UPLOAD_BYTES + 1)
        if len(data) > MAX_UPLOAD_BYTES:
            self._json_response(HTTPStatus.REQUEST_ENTITY_TOO_LARGE, {"error": "File too large"})
            return

        target_name = f"{uuid.uuid4().hex}{extension}"
        target_path = self.upload_dir / target_name
        target_path.write_bytes(data)

        self._json_response(
            HTTPStatus.CREATED,
            {
                "url": f"/uploads/{target_name}",
                "filename": target_name,
            },
        )

    def _json_response(self, status: HTTPStatus, payload: dict):
        encoded = json.dumps(payload).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(encoded)))
        self.end_headers()
        self.wfile.write(encoded)

    def _proxy(self, target, include_body: bool = True):
        base, path = target
        url = f"{base}{path}"
        body = None
        if include_body:
            length = int(self.headers.get("Content-Length", "0") or "0")
            if length > 0:
                body = self.rfile.read(length)

        headers = {
            key: value
            for key, value in self.headers.items()
            if key.lower() not in {"host", "content-length", "connection", "origin"}
        }
        headers["X-Forwarded-Host"] = self.headers.get("Host", "")

        req = request.Request(url, data=body, headers=headers, method=self.command)
        try:
            with request.urlopen(req, timeout=30) as resp:
                self.send_response(resp.status)
                for key, value in resp.getheaders():
                    if key.lower() not in {"transfer-encoding", "connection", "content-length"}:
                        self.send_header(key, value)
                payload = resp.read()
                if payload and not any(k.lower() == "content-length" for k, _ in resp.getheaders()):
                    self.send_header("Content-Length", str(len(payload)))
                self.end_headers()
                if payload:
                    self.wfile.write(payload)
        except error.HTTPError as exc:
            payload = exc.read() if exc.fp else b""
            self.send_response(exc.code)
            for key, value in (exc.headers.items() if exc.headers else []):
                if key.lower() not in {"transfer-encoding", "connection", "content-length"}:
                    self.send_header(key, value)
            if payload:
                self.send_header("Content-Length", str(len(payload)))
            self.end_headers()
            if payload:
                self.wfile.write(payload)
        except Exception as exc:
            message = f"Proxy error: {exc}".encode("utf-8")
            self.send_response(HTTPStatus.BAD_GATEWAY)
            self.send_header("Content-Type", "text/plain; charset=utf-8")
            self.send_header("Content-Length", str(len(message)))
            self.end_headers()
            self.wfile.write(message)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--port", type=int, default=5500)
    parser.add_argument("--root", default=str(APP_ROOT / "FrontEndPetFinder" / "public_html"))
    args = parser.parse_args()

    root = os.path.abspath(args.root)
    UPLOAD_DIR.mkdir(parents=True, exist_ok=True)
    mimetypes.add_type("application/javascript", ".js")

    def handler(*handler_args, **handler_kwargs):
        PetFinderHandler(*handler_args, directory=root, **handler_kwargs)

    server = ThreadingHTTPServer(("0.0.0.0", args.port), handler)
    print(f"Serving PetFinder frontend on http://127.0.0.1:{args.port}")
    print(f"Frontend root: {root}")
    print(f"Uploads dir: {UPLOAD_DIR}")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("Shutting down...")
    finally:
        server.server_close()


if __name__ == "__main__":
    main()

