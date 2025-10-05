package org.example.tourbooking.server.auth;

import org.json.JSONObject;

public class AuthHandler {

    public static String handleMessage(String message) {
        System.out.println("📩 [AuthHandler] Nhận message từ client: " + message);

        JSONObject request;
        JSONObject response = new JSONObject();

        try {
            request = new JSONObject(message);
        } catch (Exception e) {
            System.err.println("❌ [AuthHandler] Lỗi parse JSON: " + e.getMessage());
            response.put("status", "error");
            response.put("message", "Dữ liệu không hợp lệ!");
            return response.toString();
        }

        String action = request.optString("action", "");
        System.out.println("👉 [AuthHandler] Action = " + action);

        switch (action.toLowerCase()) {
            case "login":
                String email = request.optString("email", "");
                String password = request.optString("password", "");

                System.out.println("🔑 [AuthHandler] Thử đăng nhập với email=" + email + ", password=" + password);

                // TODO: sau này gọi CustomerDAO check DB
                if (email.equals("test@example.com") && password.equals("123")) {
                    response.put("status", "success");
                    response.put("message", "Đăng nhập thành công");
                } else {
                    response.put("status", "error");
                    response.put("message", "Sai email hoặc mật khẩu");
                }
                break;

            case "register":
                System.out.println("📝 [AuthHandler] Xử lý đăng ký");
                // TODO: gọi CustomerDAO insert DB
                response.put("status", "success");
                response.put("message", "Đăng ký thành công");
                break;

            default:
                System.err.println("⚠️ [AuthHandler] Action không hợp lệ: " + action);
                response.put("status", "error");
                response.put("message", "Hành động không hợp lệ");
        }

        String jsonRes = response.toString();
        System.out.println("📤 [AuthHandler] Gửi response về client: " + jsonRes);

        return jsonRes;
    }
}
