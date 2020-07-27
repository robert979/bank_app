package com.proiectfinal.bankapp.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import java.sql.*;

@Repository
@Data


public class BranchRepository {
    private static final String nameIBAN = "RO98INGB0000";
    private static final String ibanTableName = "iban";
    private static final String deletedAccount = "deleted_account";


    //metode  t_IBAN
// --> creeaza automat un IBAN nou pe care il adauga la coada tabelului
// -->sterge primul IBAN din lista (cu ID-ul cel mai mic) care va fi pasat catre t_user

    @SneakyThrows
    public Integer findFirstIbanId() {
        int a = 0;
        String query = "select min(id) from " + ibanTableName;
        PreparedStatement preparedStatement = getPreparedStatement(query);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            a = rs.getInt("min(id)");
        }
        return a;
    }

    @SneakyThrows
    private void deleteFirstIban() {
        BranchRepository branchRepository = new BranchRepository();
        String query = "delete from " + ibanTableName + " where id = " + branchRepository.findFirstIbanId();
        PreparedStatement preparedStatement = getPreparedStatement(query);
        preparedStatement.executeUpdate();
    }

    @SneakyThrows
    public String getFirstIbanNo() {
        BranchRepository branchRepository = new BranchRepository();
        Long a = 0L;
        String b = new String();
        String query = "select no_iban from " + ibanTableName + " where id=+" + branchRepository.findFirstIbanId();
        PreparedStatement preparedStatement = getPreparedStatement(query);

        ResultSet rs = preparedStatement.executeQuery();
        while ((rs.next())) {
            a = rs.getLong("no_iban");
        }
        b = String.valueOf(a);
        return b;

    }

    @SneakyThrows
    public Integer findLastIbanId() {
        int id = 0;
        String query = "select max(id) from " + ibanTableName;
        PreparedStatement preparedStatement = getPreparedStatement(query);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            id = rs.getInt("max(id)");
        }
        return id;

    }

    @SneakyThrows
    private long generateNewIban() {
        BranchRepository branchRepository = new BranchRepository();
        long a = 0;
        String query = "select no_Iban from "+ibanTableName+" where id=" + branchRepository.findLastIbanId();
        PreparedStatement preparedStatement = getPreparedStatement(query);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            a = rs.getLong("no_IBAN") + 1;
        }

        return a;
    }

    @SneakyThrows
    private void saveNewIban (){
        BranchRepository branchRepository = new BranchRepository();
        String query = "insert into "+ ibanTableName + "(name_iban, no_iban) values (\'" + nameIBAN +
                "\', "+branchRepository.generateNewIban() +")";
        PreparedStatement preparedStatement = getPreparedStatement(query);

       preparedStatement.executeUpdate();


    }


    @SneakyThrows
    public String generateIbanForAccount() {
        BranchRepository branchRepository = new BranchRepository();
        String str = nameIBAN + branchRepository.getFirstIbanNo();
        branchRepository.deleteFirstIban();
        branchRepository.saveNewIban();
        return str;

    }
    //* add deleted account details (cnp & iban) to deleted_account table;
    @SneakyThrows
    public void addDeletedAccountToTable(String cnp, String iban){
        String query = "Insert into " + deletedAccount + " (cnp ,u_iban) values (?,?)";
        PreparedStatement preparedStatement = getPreparedStatement(query);
        preparedStatement.setString(1, cnp);
        preparedStatement.setString(2, iban);

        preparedStatement.executeUpdate();

    }

    private PreparedStatement getPreparedStatement(String query) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank?serverTimezone=EET",
                    "siit",
                    "siit");
            return connection.prepareStatement(query);
        } catch (SQLException throwable) {
            System.out.println("Error while getting connection");
            throw new RuntimeException("Error while getting connection", throwable);

        }
    }


    public static void main(String[] args) {
        BranchRepository branchRepository = new BranchRepository();
        //System.out.println(branchRepository.generateNewIban());

       //branchRepository.generateIbanForAccount();

       branchRepository.addDeletedAccountToTable("1800101021922","RO98INGB0000100100100113");

    }



}


