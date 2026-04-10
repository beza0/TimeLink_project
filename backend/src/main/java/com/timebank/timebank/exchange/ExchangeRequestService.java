package com.timebank.timebank.exchange;

import com.timebank.timebank.exchange.dto.CreateExchangeRequestRequest;
import com.timebank.timebank.exchange.dto.ExchangeRequestResponse;
import com.timebank.timebank.skill.Skill;
import com.timebank.timebank.skill.SkillRepository;
import com.timebank.timebank.transaction.TimeTransaction;
import com.timebank.timebank.transaction.TimeTransactionRepository;
import com.timebank.timebank.transaction.TransactionType;
import com.timebank.timebank.user.User;
import com.timebank.timebank.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExchangeRequestService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final TimeTransactionRepository timeTransactionRepository;

    public ExchangeRequestService(
            ExchangeRequestRepository exchangeRequestRepository,
            SkillRepository skillRepository,
            UserRepository userRepository,
            TimeTransactionRepository timeTransactionRepository
    ) {
        this.exchangeRequestRepository = exchangeRequestRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.timeTransactionRepository = timeTransactionRepository;
    }

    @Transactional
    public ExchangeRequestResponse createRequest(UUID skillId, CreateExchangeRequestRequest req, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new BadCredentialsException("Kullanıcı bulunamadı"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill bulunamadı"));

        if (skill.getOwner().getEmail().equals(requesterEmail)) {
            throw new IllegalArgumentException("Kendi skill'inize talep gönderemezsiniz");
        }

        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setSkill(skill);
        exchangeRequest.setRequester(requester);
        exchangeRequest.setMessage(req.getMessage().trim());
        exchangeRequest.setStatus(ExchangeRequestStatus.PENDING);

        ExchangeRequest saved = exchangeRequestRepository.save(exchangeRequest);
        return mapToResponse(saved);
    }

    public List<ExchangeRequestResponse> getMySentRequests(String userEmail) {
        return exchangeRequestRepository.findByRequesterEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ExchangeRequestResponse> getMyReceivedRequests(String userEmail) {
        return exchangeRequestRepository.findBySkillOwnerEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ExchangeRequestResponse acceptRequest(UUID requestId, String ownerEmail) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Talep bulunamadı"));

        if (!exchangeRequest.getSkill().getOwner().getEmail().equals(ownerEmail)) {
            throw new IllegalArgumentException("Bu talebi kabul etme yetkiniz yok");
        }

        if (exchangeRequest.getStatus() != ExchangeRequestStatus.PENDING) {
            throw new IllegalArgumentException("Sadece bekleyen talepler kabul edilebilir");
        }

        exchangeRequest.setStatus(ExchangeRequestStatus.ACCEPTED);
        ExchangeRequest updated = exchangeRequestRepository.save(exchangeRequest);

        return mapToResponse(updated);
    }

    @Transactional
    public ExchangeRequestResponse rejectRequest(UUID requestId, String ownerEmail) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Talep bulunamadı"));

        if (!exchangeRequest.getSkill().getOwner().getEmail().equals(ownerEmail)) {
            throw new IllegalArgumentException("Bu talebi reddetme yetkiniz yok");
        }

        if (exchangeRequest.getStatus() != ExchangeRequestStatus.PENDING) {
            throw new IllegalArgumentException("Sadece bekleyen talepler reddedilebilir");
        }

        exchangeRequest.setStatus(ExchangeRequestStatus.REJECTED);
        ExchangeRequest updated = exchangeRequestRepository.save(exchangeRequest);

        return mapToResponse(updated);
    }

    @Transactional
    public ExchangeRequestResponse completeRequest(UUID requestId, String ownerEmail) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Talep bulunamadı"));

        if (!exchangeRequest.getSkill().getOwner().getEmail().equals(ownerEmail)) {
            throw new IllegalArgumentException("Bu talebi tamamlama yetkiniz yok");
        }

        if (exchangeRequest.getStatus() != ExchangeRequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("Sadece kabul edilmiş talepler tamamlanabilir");
        }

        User provider = exchangeRequest.getSkill().getOwner();
        User requester = exchangeRequest.getRequester();
        int minutes = exchangeRequest.getSkill().getDurationMinutes();

        if (requester.getTimeCreditMinutes() < minutes) {
            throw new IllegalArgumentException("Talep sahibi yeterli zaman kredisine sahip değil");
        }

        requester.setTimeCreditMinutes(requester.getTimeCreditMinutes() - minutes);
        provider.setTimeCreditMinutes(provider.getTimeCreditMinutes() + minutes);

        exchangeRequest.setStatus(ExchangeRequestStatus.COMPLETED);

        TimeTransaction spendTx = new TimeTransaction();
        spendTx.setUser(requester);
        spendTx.setExchangeRequest(exchangeRequest);
        spendTx.setType(TransactionType.SPEND);
        spendTx.setMinutes(minutes);
        spendTx.setDescription("Hizmet alma karşılığı zaman harcandı");

        TimeTransaction earnTx = new TimeTransaction();
        earnTx.setUser(provider);
        earnTx.setExchangeRequest(exchangeRequest);
        earnTx.setType(TransactionType.EARN);
        earnTx.setMinutes(minutes);
        earnTx.setDescription("Hizmet verme karşılığı zaman kazanıldı");

        userRepository.save(requester);
        userRepository.save(provider);
        exchangeRequestRepository.save(exchangeRequest);
        timeTransactionRepository.save(spendTx);
        timeTransactionRepository.save(earnTx);

        return mapToResponse(exchangeRequest);
    }

    private ExchangeRequestResponse mapToResponse(ExchangeRequest exchangeRequest) {
        return new ExchangeRequestResponse(
                exchangeRequest.getId(),
                exchangeRequest.getSkill().getId(),
                exchangeRequest.getSkill().getTitle(),
                exchangeRequest.getRequester().getId(),
                exchangeRequest.getRequester().getFullName(),
                exchangeRequest.getSkill().getOwner().getId(),
                exchangeRequest.getSkill().getOwner().getFullName(),
                exchangeRequest.getMessage(),
                exchangeRequest.getStatus(),
                exchangeRequest.getCreatedAt()
        );
    }
}