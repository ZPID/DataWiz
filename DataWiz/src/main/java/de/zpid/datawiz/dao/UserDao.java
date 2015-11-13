package de.zpid.datawiz.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import de.zpid.datawiz.model.DataWizUser;

@Repository
public class UserDao {

  @Autowired
  private SessionFactory sessionFactory;

  public UserDao() {
  }

  public UserDao(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Transactional
  public DataWizUser findById(int id) {
    return (DataWizUser) sessionFactory.getCurrentSession().get(DataWizUser.class, id);
  }

  @Transactional
  public DataWizUser findByMail(String email) {
    Criteria crit = sessionFactory.getCurrentSession().createCriteria(DataWizUser.class);
    crit.add(Restrictions.eq("email", email));
    DataWizUser user = (DataWizUser) crit.uniqueResult();
    System.out.println(user);
    return user;
  }

  @Transactional
  public void saveOrUpdate(DataWizUser user) {
    sessionFactory.getCurrentSession().saveOrUpdate(user);
  }
}
