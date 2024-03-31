package com.cloudshare.server.service.impl;

import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.dto.requset.NoticeAddOrUpdateReqDTO;
import com.cloudshare.server.dto.requset.NoticeListPageReqDTO;
import com.cloudshare.server.dto.response.PageResponse;
import com.cloudshare.server.model.Notice;
import com.cloudshare.server.model.User;
import com.cloudshare.server.repository.NoticeRepository;
import com.cloudshare.server.service.NoticeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author novo
 * @since 2024/3/27
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeServiceImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Override
    public PageResponse<Notice> list(NoticeListPageReqDTO reqDTO) {
        Pageable pageable = PageRequest.of(reqDTO.page() - 1, reqDTO.size());
        Page<Notice> pageResp = null;
        if (StringUtils.hasText(reqDTO.title())) {
            pageResp = noticeRepository.findAllByTitleContaining(pageable, reqDTO.title());
        } else if (StringUtils.hasText(reqDTO.content())) {
            pageResp = noticeRepository.findAllByContentContaining(pageable, reqDTO.content());
        } else {
            pageResp = noticeRepository.findAll(pageable);
        }
        List<Notice> notices = pageResp.get().toList();
        return new PageResponse<>(pageResp.getTotalElements(), notices);
    }

    @Override
    public void addNotice(NoticeAddOrUpdateReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        Notice notice = new Notice();
        notice.setUserId(userId);
        notice.setTitle(reqDTO.title());
        notice.setContent(reqDTO.content());
        noticeRepository.save(notice);
    }

    @Override
    public void updateNotice(NoticeAddOrUpdateReqDTO reqDTO, Long noticeId) {
        Optional<Notice> optional = noticeRepository.findById(noticeId);
        if (optional.isPresent()) {
            Notice notice = optional.get();
            notice.setTitle(reqDTO.title());
            notice.setContent(reqDTO.content());
            noticeRepository.save(notice);
        }
    }

    @Override
    public void deleteNotices(List<Long> ids) {
        noticeRepository.deleteAllById(ids);
    }
}
