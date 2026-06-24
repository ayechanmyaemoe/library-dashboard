package com.sip.book_shop.security.dto;

import com.sip.book_shop.web.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationInfo {

    private String accessToken;
    private String refreshToken;
    private UserDTO user;
    private List<String> grantedAuthorities;
}
