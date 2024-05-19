package com.example.library.services;

import com.example.library.models.Issuance;
import com.example.library.models.Return;
import com.example.library.repositories.ReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReturnService {
    @Autowired
    private IssuanceService issuanceService;
    @Autowired
    private ReturnRepository returnRepository;
    public List<Return> getByPeriod(Date from, Date to) { return returnRepository.findByPeriod(from, to); }
    public List<Return> getByPeriodAndId(Date from, Date to, Integer userId) { return returnRepository.findByPeriodAndUserId(from, to, userId); }
    public void create(Return ret) throws Exception {
        Integer editionId = ret.getEdition().getId(), userId = ret.getUser().getId();
        Issuance issuance = issuanceService.findByIds(editionId, userId);
        issuanceService.cancel(issuance);
        ret.setStatus(issuance.getLastDay(), ret.getDay());
        returnRepository.save(ret);
    }

    public Iterable<Return> getAll() { return returnRepository.findAll(); }

    public Object getReturns(Integer userId) { return returnRepository.findByUserId(userId); }
}
