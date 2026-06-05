package com.sip.book_shop.web.rest;

import com.sip.book_shop.common.excel.ExcelGenerator;
import com.sip.book_shop.common.vo.NzDataTableInput;
import com.sip.book_shop.dto.UserDTO;
import com.sip.book_shop.entities.queryCriteria.UserQueryCriteria;
import com.sip.book_shop.vo.*;
import com.sip.book_shop.common.vo.DataTableOutput;
import com.sip.book_shop.web.rest.base.BaseResource;
import com.sip.book_shop.services.UserApiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserResource implements BaseResource<ApiResponse<DataTableOutput<UserDTO>>, ApiResponse<UserDTO>, AddUserRequest, UpdateUserRequest> {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserApiService userApiService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterUserRequest request) throws BindException {
        userApiService.register(request);
        ApiResponse<Void> responseBody = ApiResponse.created("Registered user successfully!");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            var tokenData = userApiService.getLoginData(authentication);
            return ResponseEntity.ok(ApiResponse.success("logged in successfully!", tokenData));
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new BadCredentialsException("Invalid username or password!");
        }
    }

    @Override
    public ResponseEntity<ApiResponse<DataTableOutput<UserDTO>>> findAll(NzDataTableInput dataTableInput) {
        UserQueryCriteria criteria = UserQueryCriteria.createUserQueryCriteria(dataTableInput);
        DataTableOutput<UserDTO> dataTableOutput = DataTableOutput.of(userApiService.getAllResult(criteria));
        return ResponseEntity.ok(ApiResponse.success(dataTableOutput));
    }

    @Override
    public ResponseEntity<ApiResponse<UserDTO>> findById(int id) {
        return ResponseEntity.ok(ApiResponse.success(userApiService.findById(id)));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> create(AddUserRequest request) throws BindException {
        userApiService.addNew(request);
        ApiResponse<Void> responseBody = ApiResponse.created("Created user successfully!");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> update(UpdateUserRequest request, int id) throws BindException {
        userApiService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Updated user successfully!"));
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable int id,
                                                            @Valid @RequestBody ChangePasswordRequest request) throws BindException {
        userApiService.changePassword(request, id);
        return ResponseEntity.ok(ApiResponse.success("Updated password successfully!"));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(int id) {
        userApiService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted user successfully!"));
    }

    @Override
    public ResponseEntity<byte[]> generateExcel() {
        List<UserDTO> users = userApiService.getAll();
        byte[] excelContent = ExcelGenerator.generate(users, UserDTO.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment", "user_export.xlsx");
        return ResponseEntity.ok().headers(httpHeaders).body(excelContent);
    }
}