package com.joules.controller;

import com.joules.DatabaseConnection;
import com.joules.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainController {
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField middleName;
    @FXML
    private TextField address;
    @FXML
    private TextField email;
    @FXML
    private TextField number;
    @FXML
    private RadioButton genderMale;
    @FXML
    private RadioButton genderFemale;
    @FXML
    public ToggleGroup gender;
    @FXML
    private TableView<Student> table;
    @FXML
    private TableColumn<Student, String> colFN;
    @FXML
    private TableColumn<Student, String> colLN;
    @FXML
    private TableColumn<Student, String> colMN;
    @FXML
    private TableColumn<Student, String> colEA;
    @FXML
    private TableColumn<Student, String> colAD;
    @FXML
    private TableColumn<Student, String> colPN;
    @FXML
    private TableColumn<Student, String> colGEN;

    private boolean isEditing = false;

    @FXML
    private DatabaseConnection db;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private int studentId = 0;

    public void initialize() throws SQLException {
        db = new DatabaseConnection();

        colFN.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLN.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colMN.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        colAD.setCellValueFactory(new PropertyValueFactory<>("address"));
        colEA.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPN.setCellValueFactory(new PropertyValueFactory<>("number"));
        colGEN.setCellValueFactory(new PropertyValueFactory<>("gender"));

        loadStudents();
    }

    public void loadStudents() throws SQLException {
        studentList.clear();
        String sql = "SELECT * from students";
        Statement stmt = db.getConnection().createStatement();
        ResultSet result = stmt.executeQuery(sql);

        while (result.next()) {
            Student student = new Student(
                    result.getInt("id"),
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("middle_name"),
                    result.getString("address"),
                    result.getString("email"),
                    result.getString("phone_number"),
                    result.getString("gender")
            );
            studentList.add(student);
        }

        table.setItems(studentList);
    }

    @FXML
    private void delete() {
        Student selectedStudent = table.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            String sql = "DELETE from students WHERE id = ?";
            try {
                PreparedStatement pstmt = db.getConnection().prepareStatement(sql);
                pstmt.setInt(1, selectedStudent.getId());
                pstmt.executeUpdate();
                studentList.remove(selectedStudent);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void edit() {
        Student selectedStudent = table.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            clearInputFields();
            firstName.setText(selectedStudent.getFirstName());
            lastName.setText(selectedStudent.getLastName());
            middleName.setText(selectedStudent.getMiddleName());
            address.setText(selectedStudent.getAddress());
            email.setText(selectedStudent.getEmail());
            number.setText(selectedStudent.getNumber());

            if ("Male".equalsIgnoreCase(selectedStudent.getGender())) {
                genderMale.setSelected(true);
            } else if ("Female".equalsIgnoreCase(selectedStudent.getGender())) {
                genderFemale.setSelected(true);
            } else {
                genderMale.setSelected(false);
                genderFemale.setSelected(false);
            }

            isEditing = true;
            studentId = selectedStudent.getId();
        }
    }

    @FXML
    private void save() throws SQLException {
        if (!isEditing) {
            String sql = "INSERT INTO students(first_name, last_name, middle_name, address, email, phone_number, gender) VALUES(?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, firstName.getText());
                pstmt.setString(2, lastName.getText());
                pstmt.setString(3, middleName.getText());
                pstmt.setString(4, address.getText());
                pstmt.setString(5, email.getText());
                pstmt.setString(6, number.getText());
                pstmt.setString(7, genderMale.isSelected() ? "Male" : "Female");

                if (pstmt.executeUpdate() == 1) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        Student newStudent = new Student(
                                newId,
                                firstName.getText(),
                                lastName.getText(),
                                middleName.getText(),
                                address.getText(),
                                email.getText(),
                                number.getText(),
                                genderMale.isSelected() ? "Male" : "Female"
                        );
                        studentList.add(newStudent);
                    }
                    clearInputFields();
                    loadStudents();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String sql = "UPDATE students SET first_name = ?, last_name = ?, middle_name = ?, address = ?, email = ?, phone_number = ?, gender = ? WHERE id = ?";
            try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, firstName.getText());
                pstmt.setString(2, lastName.getText());
                pstmt.setString(3, middleName.getText());
                pstmt.setString(4, address.getText());
                pstmt.setString(5, email.getText());
                pstmt.setString(6, number.getText());
                pstmt.setString(7, genderMale.isSelected() ? "Male" : "Female");
                pstmt.setInt(8, studentId);

                if (pstmt.executeUpdate() == 1) {
                    clearInputFields();
                    loadStudents();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void clearInputFields() {
        firstName.clear();
        lastName.clear();
        middleName.clear();
        address.clear();
        email.clear();
        number.clear();
        genderMale.setSelected(false);
        genderFemale.setSelected(false);
    }
}



