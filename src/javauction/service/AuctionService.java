package javauction.service;

import javauction.model.AuctionEntity;
import javauction.util.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by gpelelis on 5/7/2016.
 */
public class AuctionService {

    public boolean addAuction(AuctionEntity auction) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();
            session.save(auction);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    public AuctionEntity getAuction(Object obj) {
        Session session = HibernateUtil.getSession();
        try {
            AuctionEntity auction = null;
            if (obj instanceof String) {
                String auction_name = obj.toString();
                Query query = session.createQuery("from AuctionEntity where name='"+auction_name+"'");
                List results = query.list();
                if (results.size() > 0) {
                    auction = (AuctionEntity) results.get(0);
                }
            } else if (obj instanceof Long) {
                long aid = (long) obj;
                auction = (AuctionEntity) session.get(AuctionEntity.class, aid);
            }
            return auction;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    public List getAllAuctions(long sid){
        Session session = HibernateUtil.getSession();
        try {
            Query query = session.createQuery("from AuctionEntity where sellerId =" + sid);
            List results = query.list();
            return results;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    /* simple search: search for auctions whose names contain string name */
    public List<AuctionEntity> searchAuction(String name) {
        Session session = HibernateUtil.getSession();
        Transaction tx = null;
        List <AuctionEntity> auctions = null;
        try {
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(AuctionEntity.class);
            criteria.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
            auctions = criteria.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return auctions;
    }

    /* advanced search, using custom criteria! */
    public List<AuctionEntity> searchAuction(String[] categories, String desc, double minPrice, double maxPrice, String location) {
        Session session = HibernateUtil.getSession();
        Transaction tx = null;
        List <AuctionEntity> auctions = null;
        try {
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(AuctionEntity.class);
            /* category search */
            // TODO category search
            /* description search */
            criteria.add(Restrictions.like("description", desc, MatchMode.ANYWHERE));
            /* minPrice < price < maxPrice */
            criteria.add(Restrictions.between("buyPrice", minPrice, maxPrice));
            /* location search*/
            Criterion city = Restrictions.like("city", location, MatchMode.EXACT);
            Criterion country = Restrictions.like("country", location, MatchMode.EXACT);
            LogicalExpression cityORcountry = Restrictions.or(city, country);
            criteria.add(cityORcountry);

            auctions = criteria.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return auctions;
    }


}
