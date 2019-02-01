package fr.epsi.book;

import fr.epsi.book.domain.Book;
import fr.epsi.book.domain.Contact;
import sun.nio.cs.UTF_32;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class App {
	
	private static final String BOOK_BKP_DIR = "./resources/backup/";
	
	private static final Scanner sc = new Scanner( System.in );
	private static Book book = new Book();
	
	public static void main( String... args ) {
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
        book.addContact(contact);
        System.out.println("Nouveau contact ajouté ...");

	}
	
	public static void editContact() {
		System.out.println( "*********************************************" );
		System.out.println( "**********Modification d'un contact**********" );
        System.out.println( "*********************************************" );
            dspContacts(false);
            System.out.print("Entrer l'identifiant du contact : ");
            String id = sc.nextLine();
            Contact contact = book.getContacts().get(id);
            if (null == contact) {
                System.out.println("Aucun contact trouvé avec cet identifiant ...");
            } else {
                System.out
                    .print("Entrer le nom ('" + contact.getName() + "'; laisser vide pour ne pas mettre à jour) : ");
                String name = sc.nextLine();
                if (!name.isEmpty()) {
                    contact.setName(name);
                }
                System.out.print("Entrer l'email ('" + contact
                    .getEmail() + "'; laisser vide pour ne pas mettre à jour) : ");
                String email = sc.nextLine();
                if (!email.isEmpty()) {
                    contact.setEmail(email);
                }
                System.out.print("Entrer le téléphone ('" + contact
                    .getPhone() + "'; laisser vide pour ne pas mettre à jour) : ");
                String phone = sc.nextLine();
                if (!phone.isEmpty()) {
                    contact.setPhone(phone);
                }
                System.out.println("Le contact a bien été modifié ...");

            }
	}
	
	public static void deleteContact() {
		System.out.println( "*********************************************" );
		System.out.println( "***********Suppression d'un contact**********" );
        System.out.println( "*********************************************" );
        System.out.println( "*****1:Mémoire******2:Base de donnée*********" );
        dspContacts( false );
        System.out.print( "Entrer l'identifiant du contact : " );
        String id = sc.nextLine();
        Contact contact = book.getContacts().remove( id );
        if ( null == contact ) {
            System.out.println( "Aucun contact trouvé avec cet identifiant ..." );
        } else {
            System.out.println( "Le contact a bien été supprimé ..." );
        }
    }
	
	public static void sort() {
		int response;
		boolean first = true;
		do {
			if ( !first ) {
				System.out.println( "***********************************************" );
				System.out.println( "* Mauvais choix, merci de recommencer !       *" );
				System.out.println( "***********************************************" );
			}
			System.out.println( "*******Choix du critère*******" );
			System.out.println( "* 1 - Nom     **              *" );
			System.out.println( "* 2 - Email **                *" );
			System.out.println( "*******************************" );
			System.out.print( "*Votre choix : " );
			try {
				response = sc.nextInt();
			} catch ( InputMismatchException e ) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while ( 0 >= response || response > 2 );
		Map<String, Contact> contacts = book.getContacts();
		switch ( response ) {
			case 1:
				contacts.entrySet().stream()
						.sorted( ( e1, e2 ) -> e1.getValue().getName().compareToIgnoreCase( e2.getValue().getName() ) )
						.forEach( e -> dspContact( e.getValue() ) );
				break;
			case 2:
				
				contacts.entrySet().stream().sorted( ( e1, e2 ) -> e1.getValue().getEmail()
																	 .compareToIgnoreCase( e2.getValue().getEmail() ) )
						.forEach( e -> dspContact( e.getValue() ) );
				break;
		}
	}
	
	public static void searchContactsByName() {
		
		System.out.println( "*******************************************************************" );
		System.out.println( "************Recherche de contacts sur le nom ou l'email************" );
		System.out.println( "*******************************************************************" );
		System.out.print( "*Mot clé (1 seul) : " );
		String word = sc.nextLine();
		Map<String, Contact> subSet = book.getContacts().entrySet().stream()
										  .filter( entry -> entry.getValue().getName().contains( word ) || entry
												  .getValue().getEmail().contains( word ) )
										  .collect( HashMap::new, ( newMap, entry ) -> newMap
												  .put( entry.getKey(), entry.getValue() ), Map::putAll );
		
		if ( subSet.size() > 0 ) {
			System.out.println( subSet.size() + " contact(s) trouvé(s) : " );
			subSet.entrySet().forEach( entry -> dspContact( entry.getValue() ) );
		} else {
			System.out.println( "Aucun contact trouvé avec cet identifiant ..." );
		}
	}
	
	public static void dspContact( Contact contact ) {
		System.out.println( contact.getId() + "\t\t\t\t" + contact.getName() + "\t\t\t\t" + contact
				.getEmail() + "\t\t\t\t" + contact.getPhone() + "\t\t\t\t" + contact.getType() );
	}
	
	public static void dspContacts( boolean dspHeader ) {
		if ( dspHeader ) {
			System.out.println( "**************************************" );
			System.out.println( "********Liste de vos contacts*********" );
		}
		for ( Map.Entry<String, Contact> entry : book.getContacts().entrySet() ) {
			dspContact( entry.getValue() );
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
			System.out.println( "* 5 - Rechercher un contact          *" );
			System.out.println( "* 6 - Trier les contacts             *" );
			System.out.println( "* 7 - Sauvegarder                    *" );
			System.out.println( "* 8 - Restaurer                      *" );
            System.out.println( "* 9 - Export en CSV                  *" );
			System.out.println( "* 10 - Quitter                       *" );
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
				searchContactsByName();
				dspMainMenu();
				break;
			case 6:
				sort();
				dspMainMenu();
				break;
			case 7:
				storeContacts();
				dspMainMenu();
				break;
			case 8:
				restoreContacts();
				dspMainMenu();
				break;
            case 9:
                exportContacts();
                dspMainMenu();
                break;
		}
	}
	
	private static void storeContacts() {
		
		Path path = Paths.get( BOOK_BKP_DIR );
		if ( !Files.isDirectory( path ) ) {
			try {
				Files.createDirectory( path );
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		String backupFileName = new SimpleDateFormat( "yyyy-MM-dd-hh-mm-ss" ).format( new Date() ) + ".ser";
		try ( ObjectOutputStream oos = new ObjectOutputStream( Files
				.newOutputStream( Paths.get( BOOK_BKP_DIR + backupFileName ) ) ) ) {
			oos.writeObject( book );
			System.out.println( "Sauvegarde terminée : fichier " + backupFileName );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	//AMELIORER proposer la liste des fichiers
	private static void restoreContacts() {
		
		try ( DirectoryStream<Path> ds = Files.newDirectoryStream( Paths.get( BOOK_BKP_DIR ), "*.ser" ) ) {
            int i = 0;
            int j = 0;
            List<Path> list = new ArrayList<Path>();
            for (Path path : ds) {
                i++;
                System.out.println("Fichier numéro " + i + " : " + path);
                list.add(path);
            }
            if(list.size() != 0) {
                System.out.println("Choissisez un fichier ");
                int number = sc.nextInt();
                if (list.size() >= number && number > 0) {
                    System.out.println("Restauration du fichier : " + list.get(number - 1).getFileName());
                    try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(list.get(number - 1)))) {
                        book = (Book) ois.readObject();
                        System.out.println("Restauration terminée : fichier " + list.get(number - 1).getFileName());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Error Fichier inexistant");
                }
            }
            else{
                System.out.println("Aucun Fichier dans le dossier backup");
            }
		} catch ( IOException e ) {
			e.printStackTrace();
		}

	}
	private static void exportContacts(){
	    FileWriter fw = null;
	    try{

            String backupFileName = new SimpleDateFormat( "yyyy-MM-dd-hh-mm-ss" ).format( new Date() ) + ".csv";
	        fw = new FileWriter(backupFileName);
	        fw.append("id;nom;email;telephone;type");
	        fw.append("\n");
            //Solution 1
            try {
                for ( Map.Entry<String, Contact> entry : book.getContacts().entrySet() ) {
                    Contact c = entry.getValue();
                    System.out.println(c.getId());
                    fw.append(c.getId());
                    fw.append((";"));
                    fw.append(c.getName());
                    fw.append((";"));
                    fw.append(c.getEmail());
                    fw.append((";"));
                    fw.append(c.getPhone());
                    fw.append((";"));
                    fw.append(c.getType().toString());
                    fw.append(("\n"));
                }
                System.out.println("Export terminée : fichier (FILEWRITER) " + backupFileName);

            } catch ( IOException e ) {
                e.printStackTrace();
            }
            finally {
                fw.flush();
                fw.close();
            }
            //Solution 2
            try ( ObjectOutputStream oos = new ObjectOutputStream( Files
                .newOutputStream( Paths.get( BOOK_BKP_DIR + backupFileName ) ) ) ) {
                oos.writeChars("id;nom;email;telephone;type \n");
                for ( Map.Entry<String, Contact> entry : book.getContacts().entrySet() ) {
                    Contact c = entry.getValue();
                    oos.writeChars(c.getId());
                    oos.writeChars((";"));
                    oos.writeChars(c.getName());
                    oos.writeChars((";"));
                    oos.writeChars(c.getEmail());
                    oos.writeChars((";"));
                    oos.writeChars(c.getPhone());
                    oos.writeChars((";"));
                    oos.writeChars(c.getType().toString());
                    oos.writeChars(("\n"));
                }
                System.out.println( "Sauvegarde terminée : fichier (OutPutStream) " + backupFileName );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        catch (Exception e){
	        e.printStackTrace();
        }
    }
}
