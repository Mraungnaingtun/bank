package com.logant.BankAccountManagementSystem.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository CustomerRepository;

    public Customer createCustomer(Customer customer) {
        return CustomerRepository.save(customer);
    }

    public Customer getCustomerById(Long id) {
        return CustomerRepository.findById(id).orElse(null);
    }

    public List<Customer> getAllCustomers() {
        return CustomerRepository.findAll();
    }

//    public List<Customer> getAllCustomersWithAccounts() {
//        return CustomerRepository.findAllWithAccounts();
//    }
}