package com.appj.controller;

import com.appj.DatabaseConnection;
import com.appj.model.Student;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
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

    @FXML
    private DatabaseConnection db;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();


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
        String sql = "SELECT * from students";
        Statement stmt = db.getConnection().createStatement();
        ResultSet result = stmt.executeQuery(sql);

        while (result.next()) {
            Student student = new Student(result.getInt("id"),
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("middle_name"),
                    result.getString("address"),
                    result.getString("email"),
                    result.getString("phone_number"),
                    result.getString("gender"));
            studentList.add(student);
        }

        table.setItems(studentList);
    }

    @FXML
    private void save() throws SQLException {
        String sql = "INSERT INTO students(first_name, last_name, middle_name, address, email, phone_number, gender) VALUES(?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = this.db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, this.firstName.getText());
        pstmt.setString(2, this.lastName.getText());
        pstmt.setString(3, this.middleName.getText());
        pstmt.setString(4, this.address.getText());
        pstmt.setString(5, this.email.getText());
        pstmt.setString(6, this.number.getText());
        pstmt.setString(7, genderMale.isSelected() ? "Male" : "Female");

        pstmt.executeUpdate();
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
    }

    private void clearInputFields() {
    }
}

