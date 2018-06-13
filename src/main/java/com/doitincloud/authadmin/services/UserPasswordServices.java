package com.doitincloud.authadmin.services;

import com.doitincloud.authadmin.models.User;

public interface UserPasswordServices {

    User resetPassword(String id);

    User sendPasswordChangeCode(String id, boolean toPhone);

    User setPassword(String id, String code, String password);

    User changePassword(String id, String password, String new_password);
}
