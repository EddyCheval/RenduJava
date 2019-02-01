package fr.epsi.book;

import fr.epsi.book.domain.Book;
import fr.epsi.book.domain.Contact;
import fr.epsi.book.dal.*;
import java.sql.SQLException;
import java.util.*;

public class AppDAO {

    private static final String BOOK_BKP_DIR = "./resources/backup/";

    private static final Scanner sc = new Scanner( System.in );
    private static Book book = new Book();
    private static BookDAO bookDAO = new BookDAO();
    private static ContactDAO contactDAO = new ContactDAO();

    public static void main( String... args ) {

        System.out.println( "***********************************************" );
        System.out.println( "* Voulez-vous créer ou séléctionner un book ?*" );
        System.out.println( "***********************************************" );
        System.out.println( "*      1-Nouveau         2-Selectionner       *" );
        System.out.println( "***********************************************" );
        System.out.print( "*Votre choix : " );

        int var =sc.nextInt();
        sc.nextLine();
        if(var == 1) {
           addBook(false);
        }
        else if (var == 2){
            dspBooks(false);
        }
        dspMainMenu();
    }

    public static Contact.Type getTypeFromKeyboard() {
        int response;
        boolean first = true;
        do {
            if ( !first ) {
                System.out.println( "***********************************************" );
                System.out.println( "* Mauvais choix, merci de recommencer !       *" );
                System.out.println( "***********************************************" );
            }
            System.out.println( "*******Choix type de contact *******" );
            System.out.println( "* 1 - Pero                         *" );
            System.out.println( "* 2 - Pro                          *" );
            System.out.println( "************************************" );
            System.out.print( "*Votre choix : " );
            try {
                response = sc.nextInt() - 1;
            } catch ( InputMismatchException e ) {
                response = -1;
            } finally {
                sc.nextLine();
            }
            first = false;
        } while ( 0 != response && 1 != response );
        return Contact.Type.values()[response];
    }

    public static void addContact() {
        System.out.println( "**************************************" );
        System.out.println( "**********Ajout d'un contact**********" );
        System.out.println( "**************************************" );
        Contact contact = new Contact();
        System.out.print("Entrer le nom :");
        contact.setName(sc.nextLine());
        System.out.print("Entrer l'email :");
        contact.setEmail(sc.nextLine());
        System.out.print("Entrer le téléphone :");
        contact.setPhone(sc.nextLine());
        contact.setType(getTypeFromKeyboard());

        try {
            contactDAO.create(contact);
            book.addContact(contact);
            bookDAO.addContact(contact,book);
            System.out.println("Nouveau contact ajouté ...");
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void addBook(boolean boo) {
        System.out.println( "******************************************************" );
        System.out.println( "**Ajout d'un book                             ********" );
        System.out.println( "******************************************************" );
        Book b = new Book();
        System.out.print("Entrer le code :");
        String str = sc.nextLine();
        b.setCode(str);
        try {
            bookDAO.create(b);
            if(boo) {
                System.out.println("*********************************************");
                System.out.println("***********Changer le book actif ?***********");
                System.out.println("*******1-OUI****************2-NON************");
                System.out.println("*********************************************");
                System.out.print("*Votre choix : ");
                int val = sc.nextInt();
                System.out.println("Nouveau book ajouté ...");
                if (val == 1) {
                    book = b;
                    System.out.println("*********************************************");
                    System.out.println("************Book actif modifier**************");
                    System.out.println("*********************************************");
                }
            }
            else{
                System.out.println("Nouveau book ajouté ...");
                book = b;
            }
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void editContact() {
        System.out.println( "*********************************************" );
        System.out.println( "**********Modification d'un contact**********" );
        System.out.println( "*********************************************" );
        ContactDAO contactDAO = new ContactDAO();
        try {
            List<Contact> contacts = contactDAO.findAll();
            for (Contact ct : contacts) {
                dspContact(ct);
            }

            System.out.println("Choissisez un id pour le contact de base de donnée ...");
            System.out.print( "*Votre choix : " );

            Long idBdd = sc.nextLong();
            sc.nextLine();
            Contact cicatrice = contactDAO.findById(idBdd);
            if(null != cicatrice) {
                System.out
                    .print("Entrer le nom ('" + cicatrice
                        .getName() + "'; laisser vide pour ne pas mettre à jour) : ");
                String name = sc.nextLine();
                if (!name.isEmpty()) {
                    cicatrice.setName(name);
                }
                System.out.print("Entrer l'email ('" + cicatrice
                    .getEmail() + "'; laisser vide pour ne pas mettre à jour) : ");
                String email = sc.nextLine();
                if (!email.isEmpty()) {
                    cicatrice.setEmail(email);
                }
                System.out.print("Entrer le téléphone ('" + cicatrice
                    .getPhone() + "'; laisser vide pour ne pas mettre à jour) : ");
                String phone = sc.nextLine();
                if (!phone.isEmpty()) {
                    cicatrice.setPhone(phone);
                }
                System.out.println("Le contact a bien été modifié ...");
                contactDAO.update(cicatrice);
            }
            else{
                System.out.println("Aucun contact correspondant");
            }
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteBook() {
        System.out.println( "*********************************************" );
        System.out.println( "***********Suppression d'un book*************" );
        System.out.println( "*********************************************" );

        try {
            List<Book> books = bookDAO.findAll();
            for (Book b : books) {
                dspbook(b);
            }

            System.out.println("Choissisez un id du book de la base de donnée ...");
            System.out.print( "*Votre choix : " );
            Long idBdd = sc.nextLong();
            sc.nextLine();
            if(idBdd != Long.parseLong(book.getId())) {
                System.out.println(idBdd.toString());
                System.out.println(Long.parseLong(book.getId()));
                Book b = bookDAO.findById(idBdd);
                if(b !=null) {
                    bookDAO.remove(b);
                }
                else{
                        System.out.println("Aucun book correspondant");
                }
            }
            else {
                System.out.println( "***********************************************" );
                System.out.println( "*Annulation de l'Opération (Le book est actif)*" );
                System.out.println( "***********************************************" );
            }
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void editBook() {
        System.out.println( "*********************************************" );
        System.out.println( "**********Modification d'un book**********" );
        System.out.println( "*********************************************" );

        try {
            List<Book> books = bookDAO.findAll();
            for (Book b : books) {
                dspbook(b);
            }

            System.out.println("Choisissez un id du book de la base de données ...");
            System.out.print( "*Votre choix : " );
            Long idBdd = sc.nextLong();
            sc.nextLine();
            Book cicatrice = bookDAO.findById(idBdd);
            if(null != cicatrice) {
                System.out
                    .print("Entrer le nom ('" + cicatrice
                        .getCode() + "'; laisser vide pour ne pas mettre à jour) : ");
                String code = sc.nextLine();
                if (!code.isEmpty()) {
                    cicatrice.setCode(code);
                }
                System.out.println("Le book a bien été modifié ...");
                bookDAO.update(cicatrice);
                System.out.println( "*********************************************" );
                System.out.println( "***********Changer le book actif ?***********" );
                System.out.println( "*******1-OUI****************2-NON************" );
                System.out.println( "*********************************************" );
                System.out.print( "*Votre choix : " );
                int val = sc.nextInt();
                if(val == 1){
                    book = cicatrice;
                    System.out.println( "*********************************************" );
                    System.out.println( "************Book actif modifier**************" );
                    System.out.println( "*********************************************" );
                }
            }
            else {
                System.out.println("Auncun book correspondant");
            }
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteContact() {
        System.out.println( "*********************************************" );
        System.out.println( "***********Suppression d'un contact**********" );
        System.out.println( "*********************************************" );

        try {
            List<Contact> contacts = contactDAO.findAll();
            for (Contact ct : contacts) {
                dspContact(ct);
            }

            System.out.println("Choissisez un id pour le contact de base de donnéeS ...");
            System.out.print( "*Votre choix : " );
            Long idBdd = sc.nextLong();
            sc.nextLine();
            Contact contact = contactDAO.findById(idBdd);
            if(contact != null) {
                contactDAO.remove(contact);
                bookDAO.removeContact(contact, book);
            }
            else
            {
                System.out.println("Auncun contact correspondant");
            }
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void dspbook( Book book ) {
        System.out.println( book.getId() + "\t\t\t\t" + book.getCode());
    }

    public static void dspBooks( boolean dspHeader ) {
        if ( dspHeader ) {
            System.out.println( "**************************************" );
            System.out.println( "********Liste des books***************" );
            System.out.println( "**************************************");
        }
        try {
            int i =0;
            for (Book book : bookDAO.findAll()) {
                i++;
                dspbook(book);
            }
            if ( dspHeader && i != 0 ) {
                System.out.println("**************************************");
                System.out.println("***Souhaitez vous changer de book*****");
                System.out.println("*****1:OUI**************2:NON*********");
                System.out.println("**************************************");
                System.out.print( "*Votre choix : " );
                int res = sc.nextInt();
                sc.nextLine();
                if (res == 1) {
                    System.out.println("**************************************");
                    System.out.println("***Choississez le book sélectionner***");
                    System.out.println("**************************************");
                    System.out.print( "*Votre choix : " );
                    Long id = sc.nextLong();
                    sc.nextLine();
                    book = bookDAO.findById(id);
                    book.setId(id.toString());

                    System.out.println("**************************************");
                    System.out.println("**********Sélection effectuer*********");
                    System.out.println("**************************************");
                }
            }
            else if(i == 0){
                System.out.println("Aucun Book trouvée");
                addBook(false);
            }
            else{
                System.out.println("**************************************");
                System.out.println("***Choississez le book sélectionner***");
                System.out.println("**************************************");
                System.out.print( "*Votre choix : " );
                Long id = sc.nextLong();
                sc.nextLine();
                book = bookDAO.findById(id);
                book.setId(id.toString());

                System.out.println("**************************************");
                System.out.println("**********Sélection effectuer*********");
                System.out.println("**************************************");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void dspContact( Contact contact ) {
        System.out.println( contact.getId() + "\t\t\t\t" + contact.getName() + "\t\t\t\t" + contact
            .getEmail() + "\t\t\t\t" + contact.getPhone() + "\t\t\t\t" + contact.getType() );
    }

    public static void dspContacts( boolean dspHeader ) {
        if ( dspHeader ) {
            System.out.println( "************************************************" );
            System.out.println( "************Liste de vos contacts **************" );
            System.out.println( "*******1-Book Actif***********2-All BDD*********" );
            System.out.println( "************************************************" );
        }
        System.out.print( "*Votre choix : " );
        int i = sc.nextInt();
        sc.nextLine();
        try {
            if(i==1) {
                bookDAO.findContacts(book);
            }
            else if (i == 2){
                for (Contact c: contactDAO.findAll()
                     ) {
                    dspContact(c);
                }
            }
            else{
                System.out.println("Error Input");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        System.out.println( "**************************************" );
    }

    public static void dspMainMenu() {
        int response;
        boolean first = true;
        do {
            if ( !first ) {
                System.out.println( "***********************************************" );
                System.out.println( "* Mauvais choix, merci de recommencer !       *" );
                System.out.println( "***********************************************" );
            }
            System.out.println( "**************************************" );
            System.out.println( "*****************Menu*****************" );
            System.out.println( "* 1 - Ajouter un contact             *" );
            System.out.println( "* 2 - Modifier un contact            *" );
            System.out.println( "* 3 - Supprimer un contact           *" );
            System.out.println( "* 4 - Lister les contacts            *" );
            System.out.println( "* 5 - Ajouter un book                *" );
            System.out.println( "* 6 - Modifier un book               *" );
            System.out.println( "* 7 - Supprimer un book              *" );
            System.out.println( "* 8 - Selectionner ou Lister un book *" );
            System.out.println( "* 9 - Quitter                        *" );
            System.out.println( "**************************************" );
            System.out.print( "*Votre choix : " );
            try {
                response = sc.nextInt();
            } catch ( InputMismatchException e ) {
                response = -1;
            } finally {
                sc.nextLine();
            }
            first = false;
        } while ( 1 > response || 9 < response );
        switch ( response ) {
            case 1:
                addContact();
                dspMainMenu();
                break;
            case 2:
                editContact();
                dspMainMenu();
                break;
            case 3:
                deleteContact();
                dspMainMenu();
                break;
            case 4:
                dspContacts( true );
                dspMainMenu();
                break;
            case 5:
                addBook(true);
                dspMainMenu();
                break;
            case 6:
                editBook();
                dspMainMenu();
                break;
            case 7:
                deleteBook();
                dspMainMenu();
                break;
            case 8:
                dspBooks(true);
                dspMainMenu();
                break;

        }
    }
}
