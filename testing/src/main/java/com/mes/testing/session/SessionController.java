package com.mes.testing.session;

import org.springframework.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/session")
public class SessionController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request, HttpSession session) {
        session.setAttribute("USER", request.getUsername());
        session.setAttribute("ROLE", request.getRole());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "登入成功");
        response.put("sessionId", session.getId());
        response.put("username", request.getUsername());
        return response;
    }

    @GetMapping("/user")
    public Map<String, Object> getUser(HttpSession session) {
        String username = (String) session.getAttribute("USER");
        String role = (String) session.getAttribute("ROLE");
        
        Map<String, Object> response = new HashMap<>();
        if (username != null) {
            response.put("username", username);
            response.put("role", role);
            response.put("loggedIn", true);
        } else {
            response.put("loggedIn", false);
        }
        return response;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        String username = (String) session.getAttribute("USER");
        session.invalidate();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "已登出");
        response.put("previousUser", username);
        return response;
    }

    @GetMapping("/attributes")
    public Map<String, Object> getAllAttributes(HttpSession session) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", session.getId());
        attributes.put("creationTime", session.getCreationTime());
        attributes.put("lastAccessedTime", session.getLastAccessedTime());
        attributes.put("maxInactiveInterval", session.getMaxInactiveInterval());
        attributes.put("USER", session.getAttribute("USER"));
        attributes.put("ROLE", session.getAttribute("ROLE"));
        return attributes;
    }

    @PostMapping("/set-attribute")
    public Map<String, Object> setAttribute(@RequestParam String key, 
                                            @RequestParam String value,
                                            HttpSession session) {
        session.setAttribute(key, value);
        
        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("value", value);
        response.put("message", "屬性已設定");
        return response;
    }

    public static class LoginRequest {
        private String username;
        private String role;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
