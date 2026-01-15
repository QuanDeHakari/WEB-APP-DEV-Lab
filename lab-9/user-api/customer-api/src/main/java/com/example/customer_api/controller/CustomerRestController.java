package com.example.customer_api.controller;

import com.example.customer_api.dto.CustomerRequestDTO;
import com.example.customer_api.dto.CustomerResponseDTO;
import com.example.customer_api.dto.CustomerUpdateDTO;
import com.example.customer_api.entity.CustomerStatus;
import com.example.customer_api.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")  // Allow CORS for frontend
public class CustomerRestController {
    
    private final CustomerService customerService;
    
    @Autowired
    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    // GET all customers
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCustomers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "fullName") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Page<CustomerResponseDTO> customerPage = customerService.getAllCustomers(page, size, sort);

        Map<String, Object> response = new HashMap<>();
        response.put("customers", customerPage.getContent());
        response.put("currentPage", customerPage.getNumber());
        response.put("totalItems", customerPage.getTotalElements());
        response.put("totalPages", customerPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
    
    // GET customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    
    // POST create new customer
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO createdCustomer = customerService.createCustomer(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }
    
    // PUT update customer
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, requestDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    // Patch update customer
    @PatchMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> partialUpdateCustomer(
        @PathVariable Long id,
        @Valid @RequestBody CustomerUpdateDTO dto) {

        CustomerResponseDTO updated = customerService.partialUpdateCustomer(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    // DELETE customer
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Customer deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    // GET search customers
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomers(@RequestParam String keyword) {
        List<CustomerResponseDTO> customers = customerService.searchCustomers(keyword);
        return ResponseEntity.ok(customers);
    }
    
    // GET customers by status
    @GetMapping("/status/{status}")
        public ResponseEntity<List<CustomerResponseDTO>> getCustomersByStatus(@PathVariable String status) {
        CustomerStatus enumStatus = CustomerStatus.valueOf(status.toUpperCase());
        List<CustomerResponseDTO> customers = customerService.getCustomersByStatus(enumStatus);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<List<CustomerResponseDTO>> advancedSearch(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String status) {
        CustomerStatus enumStatus = CustomerStatus.valueOf(status.toUpperCase());
        List<CustomerResponseDTO> customers = customerService.advancedSearch(name, email, enumStatus);
        return ResponseEntity.ok(customers);
    }

}
