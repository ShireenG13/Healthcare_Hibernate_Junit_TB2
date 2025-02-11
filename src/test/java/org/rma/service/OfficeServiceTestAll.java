package org.rma.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rma.model.Doctor;
import org.rma.model.Office;
import org.rma.repository.OfficeRepositoryImpl;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ensures tests run in order

public class OfficeServiceTestAll {

        private OfficeService officeService;
        private SessionFactory sessionFactory;
        private Session session;
        private Transaction transaction;

        private static Office office; // Shared patient instance
        private static int officeId; // Shared patient ID
        private static Doctor doctor;

        @BeforeAll
        public void setUp() {
            sessionFactory = new Configuration().configure("hibernate-test.cfg.xml").buildSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            OfficeRepositoryImpl officeRepository = new OfficeRepositoryImpl(sessionFactory);
            officeService = new OfficeService(officeRepository);
        }

        @AfterAll
        public void tearDown() {
            if (transaction != null) {
                transaction.rollback(); // Ensures test data isn't persisted
            }
            if (session != null) {
                session.close();
            }
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }

        @Test
        @Order(1)
        public void testCreateOffice() {
            //Arrange
            office = new Office();
            office.setLocation("Jakarta");
            office.setPhone("1234567899");
            doctor = new Doctor();
            doctor.setDoctorId(8);
            office.setDoctor(doctor);

            //Act
            officeService.createOffice(office);
            officeId = office.getOfficeId();//Store for future tests

            // Assert
            assertTrue(officeId > 0, "Office should be created with a valid ID.");
        }

        @Test
        @Order(2)
        public void testGetOfficeById() {
            // Act
            Office retrievedOffice = officeService.getOfficeById(officeId);

            // Assert
            assertNotNull(retrievedOffice, "Office should exist in the database.");
            assertEquals("Jakarta", retrievedOffice.getLocation());
        }

        @Test
        @Order(3)
        public void testUpdateOffice() {
            // Arrange
            Office existingOffice = officeService.getOfficeById(officeId);
            assertNotNull(existingOffice, "Office should exist before updating.");

            existingOffice.setPhone("777-777-7777");

            // Act
            officeService.updateOffice(existingOffice);
            Office updatedOffice = officeService.getOfficeById(officeId);

            // Assert
            assertNotNull(updatedOffice, "Updated office should exist.");
            assertEquals("777-777-7777", updatedOffice.getPhone());
        }

        @Test
        @Order(4)
        public void testDeleteOffice() {
            // Act
            officeService.deleteOffice(officeId);
            Office deletedOffice = officeService.getOfficeById(officeId);

            // Assert
            assertNull(deletedOffice, "Office should be deleted.");
        }

        @Test
        @Order(5)
        public void testGetAllOffices() {
            // Arrange
            Office office1 = new Office();
            office1.setLocation("Timbuktu");
            office1.setPhone("1234567889");
            Doctor doctor1 = new Doctor();
            doctor1.setDoctorId(8);
            office1.setDoctor(doctor1);

            officeService.createOffice(office1);

            Office office2 = new Office();
            office2.setLocation("Addis Ababa");
            office2.setPhone("1234567888");
            Doctor doctor2 = new Doctor();
            doctor2.setDoctorId(25);
            office2.setDoctor(doctor2);

            officeService.createOffice(office2);

            // Act
            List<Office> offices = officeService.getAllOffices();

            // Assert
            assertNotNull(offices, "Office list should not be null.");
            assertFalse(offices.isEmpty(), "Offices list should not be empty.");
            assertTrue(offices.size() >= 2, "At least two offices should exist.");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Paris", "London" , "NewYork", "Memphis"})
        @Order(6)
        public void testCreateOfficeWithDifferentLocations(String location) {
            // Arrange
            Office office = new Office();
            office.setLocation(location);
            office.setPhone("1234567888");
            Doctor doctor = new Doctor();
            doctor.setDoctorId(30);
            office.setDoctor(doctor);

            officeService.createOffice(office);

            // Act
            officeService.createOffice(office);
            Office retrievedOffice = officeService.getOfficeById(office.getOfficeId());

            // Assert
            assertNotNull(retrievedOffice, "Office should be created.");
            assertEquals(location, retrievedOffice.getLocation(), "Office name should match.");
        }
}


