package com.sip.book_shop.web.rest;

import com.sip.book_shop.common.excel.ExcelGenerator;
import com.sip.book_shop.dto.RoleDTO;
import com.sip.book_shop.entities.queryCriteria.RoleQueryCriteria;
import com.sip.book_shop.common.vo.NzDataTableInput;
import com.sip.book_shop.vo.ApiResponse;
import com.sip.book_shop.common.vo.DataTableOutput;
import com.sip.book_shop.web.rest.base.BaseResource;
import com.sip.book_shop.services.RoleApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleResource implements BaseResource<ApiResponse<DataTableOutput<RoleDTO>>, ApiResponse<RoleDTO>, RoleDTO, RoleDTO> {

    @Autowired
    private RoleApiService roleApiService;

    @Override
    public ResponseEntity<ApiResponse<DataTableOutput<RoleDTO>>> findAll(NzDataTableInput dataTableInput) {
        RoleQueryCriteria criteria = RoleQueryCriteria.createRoleQueryCriteria(dataTableInput);
        DataTableOutput<RoleDTO> dataTableOutput = DataTableOutput.of(roleApiService.getAllResult(criteria));
        return ResponseEntity.ok(ApiResponse.success(dataTableOutput));
    }

    @Override
    public ResponseEntity<ApiResponse<RoleDTO>> findById(int id) {
        return ResponseEntity.ok(ApiResponse.success(roleApiService.findById(id)));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> create(RoleDTO request) throws BindException {
        roleApiService.addNew(request);
        ApiResponse<Void> responseBody = ApiResponse.created("Created role successfully!");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> update(RoleDTO request, int id) throws BindException {
        roleApiService.update(request, id);
        return ResponseEntity.ok(ApiResponse.success("Updated role successfully!"));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(int id) {
        roleApiService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted role successfully!"));
    }

    @Override
    public ResponseEntity<byte[]> generateExcel() {
        List<RoleDTO> roles = roleApiService.getAll();
        byte[] excelContent = ExcelGenerator.generate(roles, RoleDTO.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment", "role_export.xlsx");
        return ResponseEntity.ok().headers(httpHeaders).body(excelContent);
    }
}
