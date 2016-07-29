package org.ttw.basic.dao;

import org.springframework.stereotype.Repository;
import org.ttw.basic.dao.BaseDao;
import org.ttw.basic.model.User;

@Repository("userDao")
public class UserDao extends BaseDao<User> implements IUserDao {


}
