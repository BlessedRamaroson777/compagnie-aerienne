package com.airline.airline_app.service;

import com.airline.airline_app.entity.Client;
import com.airline.airline_app.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService extends GenericServiceImpl<Client, Long> {
    
    private final ClientRepository clientRepository;
    
    @Override
    protected JpaRepository<Client, Long> getRepository() {
        return clientRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<Client> getSpecificationExecutor() {
        return clientRepository;
    }
    
    public List<Client> findAll(Specification<Client> specification) {
        return clientRepository.findAll(specification);
    }
}
