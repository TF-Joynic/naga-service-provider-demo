package indi.joynic.service.impl;

import indi.joynic.service.UserInfoService;

public class UserInfoServiceImpl implements UserInfoService {
    @Override
    public String getUserAvatarImageRelativeUrl(Long userId) {
        return "/u/1Dsx-j001.jpg";
    }
}
