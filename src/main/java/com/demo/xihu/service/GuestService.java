package com.demo.xihu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.xihu.dto.GuestListDTO;
import com.demo.xihu.entity.Guest;
import com.demo.xihu.vo.GuestShowVO;

import java.util.List;

public interface GuestService extends IService<Guest> {

    List<GuestShowVO> getGuestsList(GuestListDTO guestListDTO);

    List<GuestShowVO> getGuests();
}
