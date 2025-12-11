package idvey.testapi.blocage;


import idvey.testapi.common.PageResponse;
import idvey.testapi.tache.Tache;
import idvey.testapi.tache.TacheRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlocageService {

    private final BlocageRepository blocageRepository;
    private final TacheRepository tacheRepository;
    private final BlocageMapper blocageMapper;

    public Integer createBlocage(BlocageRequest request) {
        Blocage blocage = blocageMapper.toBlocage(request);

        // Associer la tâche si fournie
        if (request.getTacheId() != null) {
            Tache tache = tacheRepository.findById(request.getTacheId())
                    .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'ID: " + request.getTacheId()));
            blocage.setTache(tache);
        }

        return blocageRepository.save(blocage).getId();
    }

    public BlocageResponse findBlocageById(Integer id) {
        return blocageRepository.findById(id)
                .map(blocageMapper::toBlocageResponse)
                .orElseThrow(() -> new EntityNotFoundException("Blocage non trouvé avec l'ID: " + id));
    }

    public PageResponse<BlocageResponse> findAllBlocages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateSignalement").descending());
        Page<Blocage> blocages = blocageRepository.findAll(pageable);
        List<BlocageResponse> blocageResponses = blocages.stream()
                .map(blocageMapper::toBlocageResponse)
                .toList();

        return new PageResponse<>(
                blocageResponses,
                blocages.getNumber(),
                blocages.getSize(),
                blocages.getTotalElements(),
                blocages.getTotalPages(),
                blocages.isFirst(),
                blocages.isLast()
        );
    }

    public PageResponse<BlocageResponse> findBlocagesWithFilters(
            int page, int size, String titre, blocstatut statut, Integer priorite) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateSignalement").descending());
        Page<Blocage> blocages = blocageRepository.findBlocagesWithFilters(titre, statut, priorite, pageable);
        List<BlocageResponse> blocageResponses = blocages.stream()
                .map(blocageMapper::toBlocageResponse)
                .toList();

        return new PageResponse<>(
                blocageResponses,
                blocages.getNumber(),
                blocages.getSize(),
                blocages.getTotalElements(),
                blocages.getTotalPages(),
                blocages.isFirst(),
                blocages.isLast()
        );
    }

    public PageResponse<BlocageResponse> findBlocagesByStatut(int page, int size, blocstatut statut) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateSignalement").descending());
        Page<Blocage> blocages = blocageRepository.findByStatut(statut, pageable);
        List<BlocageResponse> blocageResponses = blocages.stream()
                .map(blocageMapper::toBlocageResponse)
                .toList();

        return new PageResponse<>(
                blocageResponses,
                blocages.getNumber(),
                blocages.getSize(),
                blocages.getTotalElements(),
                blocages.getTotalPages(),
                blocages.isFirst(),
                blocages.isLast()
        );
    }

    public BlocageResponse updateBlocage(Integer id, BlocageRequest request) {
        Blocage blocage = blocageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blocage non trouvé avec l'ID: " + id));

        blocageMapper.updateBlocageFromRequest(request, blocage);

        // Associer la tâche si fournie
        if (request.getTacheId() != null) {
            Tache tache = tacheRepository.findById(request.getTacheId())
                    .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'ID: " + request.getTacheId()));
            blocage.setTache(tache);
        } else {
            blocage.setTache(null);
        }

        return blocageMapper.toBlocageResponse(blocageRepository.save(blocage));
    }

    public BlocageResponse changeStatut(Integer id, blocstatut nouveauStatut) {
        Blocage blocage = blocageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blocage non trouvé avec l'ID: " + id));

        blocage.setStatut(nouveauStatut);

        // Si le statut devient RESOLU, ajouter la date de résolution
        if (nouveauStatut == blocstatut.RESOLU && blocage.getDateResolution() == null) {
            blocage.setDateResolution(LocalDate.now());
        }

        return blocageMapper.toBlocageResponse(blocageRepository.save(blocage));
    }



    public void deleteBlocage(Integer id) {
        if (!blocageRepository.existsById(id)) {
            throw new EntityNotFoundException("Blocage non trouvé avec l'ID: " + id);
        }
        blocageRepository.deleteById(id);
    }
}
