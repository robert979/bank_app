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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
@Data


public class BranchRepository {
    private static final String nameIBAN = "RO98INGB0000";
    private static final String ibanTableName = "iban";
    private static final String deletedAccount = "deleted_account";
    private static final String cardNumberTable = "card_number";
    private static final String deletedCard = "deleted_card";
    private static final String cardTable = "card";


    //metode  IBAN
// --> creeaza automat un IBAN nou pe care il adauga la coada tabelului
// -->sterge primul IBAN din lista (cu ID-ul cel mai mic) care va fi pasat catre tabel account

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
        long a = 0L;
        String query = "select no_iban from " + ibanTableName + " where id=" + branchRepository.findFirstIbanId();
        PreparedStatement preparedStatement = getPreparedStatement(query);

        ResultSet rs = preparedStatement.executeQuery();
        while ((rs.next())) {
            a = rs.getLong("no_iban");
        }
        return String.valueOf(a);

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
        String query = "select no_Iban from " + ibanTableName + " where id=" + branchRepository.findLastIbanId();
        PreparedStatement preparedStatement = getPreparedStatement(query);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            a = rs.getLong("no_IBAN") + 1;
        }

        return a;
    }

    @SneakyThrows
    private void saveNewIban() {
        BranchRepository branchRepository = new BranchRepository();
        String query = "insert into " + ibanTableName + "(name_iban, no_iban) values (\'" + nameIBAN +
                "\', " + branchRepository.generateNewIban() + ")";
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
    public void addDeletedAccountToTable(String cnp, String iban) {
        String query = "Insert into " + deletedAccount + " (cnp ,u_iban) values (?,?)";
        PreparedStatement preparedStatement = getPreparedStatement(query);
        preparedStatement.setString(1, cnp);
        preparedStatement.setString(2, iban);

        preparedStatement.executeUpdate();
    }
    //add deleted card details (iban & card number) to deleted_card table;
    @SneakyThrows
    public void addDeletedCardToTable(String iban, long cardNumber){
        String query = "insert into " + deletedCard + "(u_iban, card_number) values (?, ?) ";
        PreparedStatement preparedStatement =getPreparedStatement(query);
        preparedStatement.setString(1, iban);
        preparedStatement.setLong(2, cardNumber);

        preparedStatement.executeUpdate();
    }
    // generate new card numbers using card_number table -->delete the first one, which will be sent to card table
    //and adds a new card number at the end

    public Long passNewCardNumberToNewCard() {
        Long card_number = findFirstCardNumber();
        saveNewCardNumber();
        deleteFirstCardNumber();
        return card_number;
    }

    @SneakyThrows
    private void deleteFirstCardNumber() {
        String query = "delete from " + cardNumberTable + " where id =" + findFirstIdCardNumber();
        PreparedStatement preparedStatement = getPreparedStatement(query);
        preparedStatement.executeUpdate();

    }

    @SneakyThrows
    private void saveNewCardNumber() {
        String query = " insert into " + cardNumberTable + " (card_number) values (" + generateNewCardNumber() + ")";
        PreparedStatement preparedStatement = getPreparedStatement(query);
        preparedStatement.executeUpdate();

    }

    @SneakyThrows
    private long generateNewCardNumber() {
        long card_number = 0L;
        String query = "select card_number from " + cardNumberTable + " where id = " + findLastIdCardNumber();
        PreparedStatement preparedStatement = getPreparedStatement(query);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            card_number = rs.getLong("card_number");
        }
        return card_number + 1;
    }

    @SneakyThrows
    private int findLastIdCardNumber() {
        int id = 0;
        String query = "Select max(id) from " + cardNumberTable;
        PreparedStatement preparedStatement = getPreparedStatement(query);

        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            id = rs.getInt("max(id)");
        }
        return id;
    }

    @SneakyThrows
    private long findFirstCardNumber() {
        long card_number = 0L;
        String query = "select card_number from " + cardNumberTable + " where id=" + findFirstIdCardNumber();
        PreparedStatement preparedStatement = getPreparedStatement(query);

        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            card_number = rs.getLong("card_number");
        }
        return card_number;
    }

    @SneakyThrows
    private int findFirstIdCardNumber() {
        int id = 0;
        String query = "select min(id) from " + cardNumberTable;
        PreparedStatement preparedStatement = getPreparedStatement(query);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            id = rs.getInt("min(id)");
        }
        return id;
    }

    @SneakyThrows
    public void setLastUpdate (Date date, long id){
        String query = "Update " + cardTable + " set Last_updated = ? where id = ?";
        PreparedStatement preparedStatement = getPreparedStatement(query);
        preparedStatement.setDate(1,date);
        preparedStatement.setLong(2, id);
        preparedStatement.executeUpdate();


    }

    @SneakyThrows
    public long findIdByCardNumber(long cardNumber){
        long id=0;
        String query = "select id from " + cardTable + " id where card_number=?";
        PreparedStatement preparedStatement = getPreparedStatement(query);
        preparedStatement.setLong(1, cardNumber);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()){
            id=rs.getLong("id");

        }
        return id;
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
    @SneakyThrows
    public void blockCard(long id){
        String query = "update " + cardTable + " set status = 'blocked' where id =?";
        PreparedStatement preparedStatement = getPreparedStatement(query);
        preparedStatement.setLong(1, id);

        preparedStatement.executeUpdate();

    }


    public static void main(String[] args) {
        BranchRepository branchRepository=new BranchRepository();
        System.out.println("doar pt test " + branchRepository.findIdByCardNumber(1234123412340000L));


    }


}


