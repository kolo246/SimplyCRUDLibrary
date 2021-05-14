package com.tango.down.clients;

import com.tango.down.books.Books;
import com.tango.down.users.Users;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.Scanner;

public class ConsoleRestClient {
    public static void main(String[] args) {
        int choice = 0;
        do {
            Scanner sc = new Scanner(System.in);
            System.out.println("------------------------------------------");
            System.out.println("--------Client for USERS and BOOKS--------");
            System.out.println("------------------------------------------");
            System.out.println("-------------CHOICE ACTION----------------");
            System.out.println("------------------------------------------");
            System.out.println("1. USER");
            System.out.println("2. BOOKS");
            System.out.println("3. EXIT");
            System.out.println("...");
            choice = sc.nextInt();
            menu(choice);
        } while (choice != 3);
        //TODO make args from command line
    }

    public static void menu(int choice) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder().rootUri("http://localhost:8080/");
        RestClient client = new RestClient(restTemplateBuilder);
        Scanner scanner = new Scanner(System.in);
        int action = 0;
        switch (choice) {
            case 1:
                do {
                    System.out.println("-------------CHOICE ACTION----------------");
                    System.out.println("1. POST USER");
                    System.out.println("2. GET ALL USERS WITH PAGINATION");
                    System.out.println("3. GET USER BY ID");
                    System.out.println("4. UPDATE USER NAME");
                    System.out.println("5. DELETE USER BY ID");
                    System.out.println("6. EXIT");
                    System.out.println("...");
                    action = scanner.nextInt();
                    switch (action) {
                        case 1://post user
                            System.out.println("1. POST USER");
                            Users postUser = new Users();
                            System.out.println("Name :");
                            String var = scanner.next();
                            postUser.setName(var);
                            System.out.println("Email :");
                            var = scanner.next();
                            postUser.setEmail(var);
                            System.out.println("Phone number: ");
                            var = scanner.next();
                            postUser.setPhoneNumber(var);
                            System.out.println("Age: ");
                            int age = scanner.nextInt();
                            postUser.setAge(age);
                            client.createUser(postUser);
                            break;
                        case 2://get all users
                            System.out.println("2. GET ALL USERS WITH PAGINATION");
                            System.out.println("Pages: ");
                            int pages = scanner.nextInt();
                            System.out.println("Size: ");
                            int size = scanner.nextInt();
                            client.getUsers(pages, size);
                            break;
                        case 3://get user by id
                            System.out.println("3. GET USER BY ID");
                            System.out.println("ID: ");
                            Long id = scanner.nextLong();
                            client.getUser(id);
                            break;
                        case 4://patch user
                            System.out.println("4. UPDATE USER NAME");
                            System.out.println("ID: ");
                            id = scanner.nextLong();
                            System.out.println("New Name: ");
                            var = scanner.next();
                            String patchBody = "[\n" +
                                    "    {\n" +
                                    "        \"op\":\"replace\",\n" +
                                    "        \"path\":\"/name\",\n" +
                                    "        \"value\":\" " + var + "\"\n" +
                                    "    }\n" +
                                    "]";
                            client.updateUser(id, patchBody);
                            break;
                        case 5://delete user by id
                            System.out.println("5. DELETE USER BY ID");
                            System.out.println("ID: ");
                            id = scanner.nextLong();
                            client.deleteUser(id);
                            break;
                        case 6:
                            break;
                    }
                } while (action != 6);
                break;
            case 2:
                do {
                    System.out.println("-------------CHOICE ACTION----------------");
                    System.out.println("1. POST BOOK");
                    System.out.println("2. GET ALL BOOKS WITH PAGINATION");
                    System.out.println("3. GET BOOK BY ID");
                    System.out.println("4. RESERVE BOOK");
                    System.out.println("5, BORROW BOOK");
                    System.out.println("6. UPDATE BOOK NAME");
                    System.out.println("7. DELETE USER BY ID");
                    System.out.println("8. EXIT");
                    System.out.println("...");
                    action = scanner.nextInt();
                    switch (action) {
                        case 1://post books
                            Books postBook = new Books();
                            System.out.println("1. POST BOOKS");
                            System.out.println("Title: ");
                            String var = scanner.next();
                            postBook.setTitle(var);
                            System.out.println("Author: ");
                            var = scanner.next();
                            postBook.setAuthor(var);
                            System.out.println("Pages: ");
                            int pages = scanner.nextInt();
                            postBook.setPages(pages);
                            client.createBook(postBook);
                            break;
                        case 2://get all books
                            System.out.println("2. GET ALL BOOKS WITH PAGINATION");
                            System.out.println("Pages: ");
                            pages = scanner.nextInt();
                            System.out.println("Size: ");
                            int size = scanner.nextInt();
                            System.out.println("Is Borrow ?: ");
                            boolean isBorrow = scanner.nextBoolean();
                            client.fetchAllBooks(pages, size, isBorrow);
                            break;
                        case 3://get book by id
                            System.out.printf("3. GET BOOK BY ID");
                            System.out.println("ID: ");
                            Long id = scanner.nextLong();
                            client.fetchBookById(id);
                            break;
                        case 4://reserve book
                            System.out.println("4. RESERVE BOOK");
                            System.out.println("ID Book: ");
                            id = scanner.nextLong();
                            System.out.println("ID User: ");
                            Long idUser = scanner.nextLong();
                            client.reserveBook(id, idUser);
                            break;
                        case 5://borrow book
                            System.out.println("5. BORROW BOOK");
                            System.out.println("ID Book: ");
                            id = scanner.nextLong();
                            System.out.println("ID User: ");
                            idUser = scanner.nextLong();
                            client.borrowBook(id, idUser);
                            break;
                        case 6://patch book
                            System.out.println("6. UPDATE BOOK TITLE");
                            System.out.println("ID Book: ");
                            id = scanner.nextLong();
                            System.out.println("New Title: ");
                            var = scanner.next();
                            String patchBody = "[\n" +
                                    "    {\n" +
                                    "        \"op\":\"replace\",\n" +
                                    "        \"path\":\"/name\",\n" +
                                    "        \"value\":\" " + var + "\"\n" +
                                    "    }\n" +
                                    "]";
                            client.updateBookTitle(id, patchBody);
                            break;
                        case 7://delete book by id
                            System.out.println("7. DELETE BOOK BY ID");
                            System.out.println("ID: ");
                            id = scanner.nextLong();
                            client.deleteBook(id);
                            break;
                    }
                } while (action != 8);
            case 3:
                break;
        }
    }

}
