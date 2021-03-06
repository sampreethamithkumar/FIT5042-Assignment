package fit5042.assx.repository;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.OverridesAttribute;

import fit5042.assx.entities.*;

@Stateless
public class StaffRepositoryImplementation implements StaffRepository {

	@PersistenceContext
	EntityManager entityManager;
	
	@Override
	public List<Integer> getStaffId() {
		List<Integer> staffId = entityManager.createNamedQuery(Staff.GET_ID).getResultList();
		return staffId;
	}

	@Override
	public void addStaff(Staff staff) {
		List<Staff> staffs = getStaff();
		if (staffs.size() >= 1) {
			int lastStaffId = staffs.get(staffs.size() - 1).getStaffId();
			staff.setStaffId(lastStaffId + 1);
		}
		else
			staff.setStaffId(1);
		entityManager.persist(staff);
		

	}

	@Override
	public List<Staff> getStaff() {
		CriteriaBuilder criteriaBuilder= entityManager.getCriteriaBuilder();
		CriteriaQuery<Staff> criteraiQuery = criteriaBuilder.createQuery(Staff.class);
		Root<Staff> rootEntry = criteraiQuery.from(Staff.class);
		CriteriaQuery<Staff> all = criteraiQuery.select(rootEntry);
		
		TypedQuery<Staff> allQuery = entityManager.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public void editStaff(Staff staff) {
		entityManager.merge(staff);
	}

	@Override
	public void removeStaff(int staffId) {
		Staff staff = entityManager.find(Staff.class, staffId);
		if(staff != null) {
			for (Customer cust: getCustomers()) {
				if (cust.getStaffId().equals(staff)) {
					CustomerContactInformation custInfo = cust.getContactInformation();
					entityManager.remove(cust);
					entityManager.remove(custInfo);					
				}	
			}
			removeStaffLoginFromUser(staff.getStaffFname());
			removeStaffLoginFromUserGroup(staff.getStaffFname());
			entityManager.remove(staff);
		}
			
	}
	
	@Override
	public Staff searchStaffById(int staffId) {
		Staff staff = entityManager.find(Staff.class, staffId);
		return staff;
	}
	
	private void removeStaffLoginFromUser(String staffName) {
		Query query = entityManager.createQuery("DELETE FROM Users u WHERE u.username = :staffName");
		query.setParameter("staffName", staffName);
		query.executeUpdate();
	}
	
	private void removeStaffLoginFromUserGroup(String staffName) {
		Query query = entityManager.createQuery("DELETE FROM UserGroup g WHERE g.userName = :staffName");
		query.setParameter("staffName", staffName);
		query.executeUpdate();
	}

	@Override
	public List<Staff> getStaffIdByFname(String staffName) {
		String queryString = "SELECT s.staffId FROM Staff s WHERE s.staffFname = :staffName";
		TypedQuery<Staff> query = entityManager.createQuery(queryString,Staff.class);
		query.setParameter("staffName", staffName);
		return query.getResultList();
	}

	@Override
	public List<Staff> getStaffName(String staffName) {
		String queryString = "SELECT s.staffFname FROM Staff s";
		TypedQuery<Staff> query = entityManager.createQuery(queryString,Staff.class);
		return query.getResultList();
		
	}
	
	private List<Customer> getCustomers() 
	{
		CriteriaBuilder criteriaBuilder= entityManager.getCriteriaBuilder();
		CriteriaQuery<Customer> criteraiQuery = criteriaBuilder.createQuery(Customer.class);
		Root<Customer> rootEntry = criteraiQuery.from(Customer.class);
		CriteriaQuery<Customer> all = criteraiQuery.select(rootEntry);
		
		TypedQuery<Customer> allQuery = entityManager.createQuery(all);
		return allQuery.getResultList();
	}

	@Override
	public List<Staff> searchStaffByFirstAndLastName(String firstName, String lastName) {
//		String queryString = "SELECT s.staffId FROM Staff s WHERE s.staffFname =:firstName and s.staffLname =:lastName";
//		TypedQuery<Staff> query = entityManager.createQuery(queryString,Staff.class);
//		query.setParameter("firstName",firstName);
//		query.setParameter("lastName",lastName);
//		return query.getResultList();
		
		List<Staff> staffs = new ArrayList<>();
		
		for (Staff staff: getStaff()) {
			if (staff.getStaffFname().equals("firstName") && staff.getStaffLname().equals("lastName")) {
				staffs.add(staff);
			}
		}
		
		return staffs;
			
	}
}
