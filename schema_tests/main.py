import sys
import requests
import schemathesis
import pytest

from hypothesis import settings, seed

# by ai
# 1. 配置信息
BASE_URL = "http://localhost:8080"
OPENAPI_URL = f"{BASE_URL}/v3/api-docs"
LOGIN_URL = f"{BASE_URL}/auth/login"

def get_access_token() -> str:
    payload = {"username": "admintest", "password": "admin123456"}
    print(f"正在登录: {LOGIN_URL} ...")
    resp = requests.post(LOGIN_URL, json=payload, timeout=30)
    resp.raise_for_status()
    data = resp.json()
    token = data.get("accessToken") or data.get("data", {}).get("accessToken")
    if not token:
        raise RuntimeError(f"未能从响应中提取到 Token, 响应: {data}")
    print("登录成功，Token获取完毕")
    return token

print(f"正在加载 Schema: {OPENAPI_URL}")
schema = schemathesis.openapi.from_url(OPENAPI_URL)
print("Schema 加载成功！")

# ✅ 推荐：用 schema.auth() 统一注入 token（比 before_call hook 更适合“动态 token”场景）
@schema.auth()
class JwtAuth:
    def get(self, case, ctx):
        return get_access_token()

    def set(self, case, token, ctx):
        case.headers = case.headers or {}
        case.headers["Authorization"] = f"Bearer {token}"

# 5. 运行测试（pytest + hypothesis）
@schema.parametrize()
@seed(42)
@settings(max_examples=50, deadline=None)
def test_api(case):
    # 只保留你想要的检查：500
    case.call_and_validate(checks=[schemathesis.checks.not_a_server_error])

if __name__ == "__main__":
    # 让你继续用 `python main.py` 的体验
    raise SystemExit(pytest.main(["-q", __file__]))
