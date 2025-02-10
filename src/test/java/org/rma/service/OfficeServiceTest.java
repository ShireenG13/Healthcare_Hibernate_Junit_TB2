package org.rma.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rma.model.Doctor;
import org.rma.model.Office;
import org.rma.model.Patient;
import org.rma.repository.OfficeRepositoryImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OfficeServiceTest {

    private OfficeService officeService;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        sessionFactory = new Configuration().configure("hibernate-test.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        OfficeRepositoryImpl officeRepository = new OfficeRepositoryImpl(sessionFactory);
        officeService = new OfficeService(officeRepository);
    }

    @AfterEach
    public void tearDown(){
        if(transaction != null){
            transaction.rollback();
        }
        if(session != null) {
            session.close();
        }
        if(sessionFactory != null){
            sessionFactory.close();
        }
    }

    @Test
    public void testCreateOffice() {
        Office office = new Office();
        office.setLocation("Hyderabad");
        office.setPhone("1234567890");
        Doctor doctor = new Doctor();
        doctor.setDoctorId(8);
        office.setDoctor(doctor);
        officeService.createOffice(office);

        Office retrievedOffice = officeService.getOfficeById(office.getOfficeId());
        assertNotNull(retrievedOffice, "Office should be created successfully.");
        assertEquals("Hyderabad", retrievedOffice.getLocation(), "Office location should match");
        assertEquals("1234567890", retrievedOffice.getPhone(), "Office phone should match");

    }

    @Test
    public void testGetOfficeById(){
        Office office = new Office();
        office.setLocation("Seoul");
        office.setPhone("1234567890");
        Doctor doctor = new Doctor();
        doctor.setDoctorId(10);
        office.setDoctor(doctor);
        officeService.createOffice(office);

        Office retrievedOffice = officeService.getOfficeById(office.getOfficeId());
        assertNotNull(retrievedOffice, "Office should exist in database.");
        assertEquals("Seoul", retrievedOffice.getLocation(), "Office location should match");
        assertEquals("1234567890", retrievedOffice.getPhone(), "Office phone should match");

    }

    @Test
    public void testGetAllOffices() {
        Office office = new Office();
        office.setLocation("Istanbul");
        office.setPhone("1234567891");
        Doctor doctor = new Doctor();
        doctor.setDoctorId(15);
        office.setDoctor(doctor);
        officeService.createOffice(office);

        Office office1 = new Office();
        office.setLocation("Dhaka");
        office.setPhone("1234567892");
        Doctor doctor1 = new Doctor();
        doctor.setDoctorId(14);
        office.setDoctor(doctor);
        officeService.createOffice(office);

        List<Office> offices = officeService.getAllOffices();

        assertNotNull(offices, "Office list should not be null.");
        assertFalse(offices.isEmpty(), "Office list should not be empty.");
        assertTrue(offices.size() >= 2, "At least two offices should be present.");
    }

    @Test
    public void testUpdateOffice(){
        Office office = new Office();
        office.setLocation("Santiago");
        office.setPhone("1234567893");
        Doctor doctor = new Doctor();
        doctor.setDoctorId(18);
        office.setDoctor(doctor);
        officeService.createOffice(office);


        // Act
        office.setPhone("1112223333"); // Update phone number
        officeService.updateOffice(office);
        Office updatedOffice = officeService.getOfficeById(office.getOfficeId());

        // Assert
        assertNotNull(updatedOffice, "Updated office should exist.");
        assertEquals("1112223333", updatedOffice.getPhone(), "Phone number should be updated.");
    }


    @Test
    public void testDeleteOffice(){
        Office office = new Office();
        office.setLocation("Buenos Aires");
        office.setPhone("1234567895");
        Doctor doctor = new Doctor();
        doctor.setDoctorId(17);
        office.setDoctor(doctor);
        officeService.createOffice(office);

        int officeId = office.getOfficeId();

        officeService.deleteOffice(officeId);
        Office deletedOffice = officeService.getOfficeById(officeId);

        assertNull(deletedOffice, "Office should be deleted.");

    }

    @ParameterizedTest
    @ValueSource(strings ={"Paris","London", "NewYork", "San Francisco", "Atlanta" })
    public void testCreateOfficeWithDifferentLocations(String location) {
        Office office = new Office();
        office.setLocation(location);
        office.setPhone("1234567898");
        Doctor doctor = new Doctor();
        doctor.setDoctorId(20);
        office.setDoctor(doctor);
        officeService.createOffice(office);

        Office retrievedOffice = officeService.getOfficeById(office.getOfficeId());

        assertNotNull(retrievedOffice, "Office should be created.");
        assertEquals(location, retrievedOffice.getLocation(), "Location should match.");
    }
}





