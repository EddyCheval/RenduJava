package fr.epsi.book.dal;

import fr.epsi.book.domain.Book;
import fr.epsi.book.domain.Contact;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BookDAO implements IDAO<Book, Long> {


    private static final String INSERT_QUERY = "INSERT INTO book (code) values (?)";
    private static final String FIND_BY_ID_QUERY = "select * from Book where id=?";
    private static final String FIND_ALL_QUERY = "select * from Book";
    private static final String UPDATE_QUERY = "UPDATE Book SET code=? WHERE id=? ";
    private static final String REMOVE_QUERY = "DELETE FROM Book WHERE id=?";
    private static final String ADD_CONTACT_QUERY = "INSERT INTO contact_book (id_book,id_contact) values (?,?)";
    private static final String DELETE_CONTACT_QUERY = "DELETE FROM contact_book WHERE id_book=? and id_contact=?";
    private static final String FIND_CONTACT_QUERY = "SELECT * FROM Contact JOIN CONTACT_book on id=id_contact where contact_book.id_book=?";
    private static final String ORACLE_ID_INSERT_QUERY = "SELECT BOOK_SEQUENCE.CURRVAL FROM BOOK";
	
	@Override
	public void create( Book o ) throws SQLException {
        Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( INSERT_QUERY, Statement.RETURN_GENERATED_KEYS );
        st.setString( 1, o.getCode() );
        st.executeUpdate();

        ResultSet rs = st.getGeneratedKeys();

        if ( rs.next() ) {
            o.setId( rs.getString( 1 ) );
        }
	}
	
	@Override
	public Book findById( Long aLong ) throws SQLException {
        Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( FIND_BY_ID_QUERY, Statement.RETURN_GENERATED_KEYS );
        st.setString( 1, aLong.toString() );
        ResultSet rs =st.executeQuery();
        while (rs.next ()){
            Book o = new Book();
            o.setId(rs.getString(1));
            o.setCode(rs.getString(2));
            return o;
        }
        return null;
	}
	
	@Override
	public List<Book> findAll() throws SQLException {
        Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( FIND_ALL_QUERY);
        ResultSet v =st.executeQuery();
        List<Book> bookList = new ArrayList<>();
        while (v.next ()) {

            Book o = new Book();
            o.setId(v.getString(1));
            o.setCode(v.getString(2));
            bookList.add(o);
        }

        return bookList;
    }
	
	@Override
	public Book update( Book o ) throws SQLException {
        Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS );
        st.setString( 1, o.getCode() );
        st.setString( 2, o.getId() );
        st.executeUpdate();
        Map<String,Contact> lc = o.getContacts();
        for (Contact c: lc.values()) {
            this.addContact(c,o);
        }
        return null;

    }
	
	@Override
	public void remove( Book o ) throws SQLException {
        Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( REMOVE_QUERY, Statement.RETURN_GENERATED_KEYS );
        st.setString( 1, o.getId() );
        st.executeUpdate();
	}
	public void addContact(Contact c,Book b) throws SQLException{
        Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( ADD_CONTACT_QUERY, Statement.RETURN_GENERATED_KEYS );
        st.setString( 1, b.getId() );
        st.setString( 2, c.getId() );
        st.executeUpdate();

    }
    public void findContacts(Book b) throws SQLException{
        Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( FIND_CONTACT_QUERY, Statement.RETURN_GENERATED_KEYS );
        st.setString( 1, b.getId() );
        ResultSet rs = st.executeQuery();
        int i =0;
        while ( rs.next() ) {
            i++;
            System.out.println( "id : " + rs.getString(1) + "\t\t\t\t Name :" + rs.getString(2));
        }
        if(i==0){
            System.out.println("No contacts Found");
        }

    }
    public void removeContact(Contact c,Book b) throws SQLException{
        Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( DELETE_CONTACT_QUERY, Statement.RETURN_GENERATED_KEYS );
        st.setString( 1, b.getId() );
        st.setString( 2, c.getId() );
        st.executeUpdate();

        System.out.println("Suppression effectuée");
    }
}
