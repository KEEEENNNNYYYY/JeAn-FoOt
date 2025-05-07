package com.fifa.app.Services;


import com.fifa.app.DAO.SeasonDAO;
import com.fifa.app.DAO.TransfertDAO;
import com.fifa.app.Entities.Season;
import com.fifa.app.Entities.Transfert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransfertService {

    private final TransfertDAO transfertDAO;

    public List<Transfert> getAll() {
        return transfertDAO.getAll();
    }


}
