package org.guce.process.atm.repositories;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.guce.core.ejb.facade.interfaces.AbstractFacade;
import org.guce.core.services.SearchFilter;
import org.guce.process.atm.entities.TypeAvtech;

public class TypeAvtechRepository extends AbstractFacade<TypeAvtech> {
    @PersistenceContext(
            unitName = "Partner-ejb-unity"
    )
    private EntityManager em;

    public TypeAvtechRepository() {
        super(TypeAvtech.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TypeAvtech> findByDomain(String domain) {
        domain = domain == null ? "" : " and " + domain;
        String qb = "Select distinct i from TypeAvtech i where i.deleted=false and i.active = true "+domain ;
        System.out.println(qb);
        TypedQuery<TypeAvtech> query = em.createQuery(qb, TypeAvtech.class);
        return query.getResultList();
    }

    public int searchCount(SearchFilter filter) {
        String qb = "Select count(distinct i) from TypeAvtech i where i.deleted=false ";
        qb = addConditions(qb, filter);
        TypedQuery<Long> query = em.createQuery(qb, Long.class);
        query = addParams(query, filter);
        return query.getResultList().get(0).intValue();
    }

    public List<TypeAvtech> search(SearchFilter filter, int start, int count, String orderField, String order) {
        if (orderField == null || orderField.isEmpty()) {
            orderField = "createDate";
            if (order == null || order.isEmpty()) {
                order = "desc";
            }
        }
        String qb = "Select distinct i from TypeAvtech i where i.deleted=false";
        qb = addConditions(qb, filter);
        qb = qb +" order by i." + orderField + " " + order;
        TypedQuery<TypeAvtech> query = em.createQuery(qb, TypeAvtech.class);
        query = addParams(query, filter);
        if(start >= 0) {
            query.setFirstResult(start);
            query.setMaxResults(count);
        }
        return query.getResultList();
    }

    public String addConditions(String sql, SearchFilter filter) {
        Map<String, Object> params = filter.getOtherFilter();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if("active".equals(key) && value != null) {
                sql = sql + " and i." + key + " = :active";
                continue;
            }
            if (value != null && (value instanceof String) && !((String) value).trim().isEmpty()) {
                sql = sql + " and i." + key + " like :" + key.replaceAll("\\.", "_");
            } else if (value != null && (value instanceof Object[])) {
                String operation = key.endsWith("_One") ? " = " : "member of";
                Object[] objs = (Object[]) value;
                                if(objs.length <= 0)
                                    continue;
                                String au = "(";
                                int i = 0;
                                for (Object obj : objs) {
                                    if (key.endsWith("_One")) {
                                        au = au + " i." + key.replaceAll("_One", "") + " = :" + key.replaceAll("\\.", "_").replaceAll("_One", "") + (i++) + " or";
                                    } else {
                                        au = au + " :" + key.replaceAll("\\.", "_").replaceAll("_One", "") + (i++) + " " + operation + " i." + key.replaceAll("_One", "") + " or";
                                    }
                                }
                                au = au.substring(0, au.length()-2) +")";
                                sql = sql + " and "+au+" ";
            } else if (value != null && !(value instanceof String)) {
                if(key.endsWith("Start")) {
                    sql = sql + " and i." + key.replace("Start", "") + " >= :" + key.replaceAll("\\.", "_");
                } else if(key.endsWith("End")) {
                    sql = sql + " and i." + key.replace("End", "") + " < :" + key.replaceAll("\\.", "_");
                } else {
                    sql = sql + " and i." + key + " = :" + key.replaceAll("\\.", "_");
                }
            }
        }
        return sql;
    }

    public TypedQuery addParams(TypedQuery query, SearchFilter filter) {
        Map<String, Object> params = filter.getOtherFilter();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if("active".equals(key) && value != null) {
                query.setParameter("active", Boolean.valueOf(value.toString()));
                continue;
            }
            if (value != null && (value instanceof String) && !((String) value).trim().isEmpty()) {
                query.setParameter(key.replaceAll("\\.", "_"), value+"%");
            } else if (value != null && (value instanceof Object[])) {
                Object[] objs = (Object[]) value;
                                if(objs.length <= 0)
                                    continue;
                                int i = 0;
                                for (Object obj : objs) {
                                    query.setParameter(key.replaceAll("\\.", "_").replaceAll("_One", "")+(i++), obj);
                                }
            } else if (value != null && !(value instanceof String)) {
                query.setParameter(key.replaceAll("\\.", "_"), value);
            }
        }
        return query;
    }
}
