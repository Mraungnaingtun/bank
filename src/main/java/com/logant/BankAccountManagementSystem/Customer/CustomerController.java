package com.logant.BankAccountManagementSystem.Customer;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/customers")
public class CustomerController {
    @Autowired
    private CustomerService CustomerService;

    @PreAuthorize("hasAnyAuthority('SCOPE_WRITE')")
    @PostMapping
    public Customer createCustomer(@Valid  @RequestBody Customer customer) {
        return CustomerService.createCustomer(customer);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return CustomerService.getCustomerById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    public List<Customer> getAllCustomers() {
        return CustomerService.getAllCustomers();

    }

//    @GetMapping("/customers-with-accounts")
//    public List<Customer> getCustomersWithAccounts() {
//        return CustomerService.getAllCustomersWithAccounts();
//    }

}