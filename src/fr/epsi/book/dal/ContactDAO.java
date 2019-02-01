package fr.epsi.book.dal;

import fr.epsi.book.domain.Contact;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO implements IDAO<Contact, Long> {
	
	private static final String INSERT_QUERY = "INSERT INTO contact (name, email, phone, type_var, type_num) values (?,?,?,?,?)";
	private static final String FIND_BY_ID_QUERY = "select * from Contact where id=?";
	private static final String FIND_ALL_QUERY = "select * from Contact";
	private static final String UPDATE_QUERY = "UPDATE contact SET name=?,email=?,phone=?,type_var=?,type_num=? where id=? ";
	private static final String REMOVE_QUERY = "DELETE FROM contact WHERE id=?";
	private static final String ORACLE_INSERTED_ID_QUERY = "SELECT CONTACT_SEQUENCE.CURRVAL from Contact";
	
	@Override
	public void create( Contact c ) throws SQLException {
		
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( INSERT_QUERY, Statement.RETURN_GENERATED_KEYS );
		st.setString( 1, c.getName() );
		st.setString( 2, c.getEmail() );
		st.setString( 3, c.getPhone() );
		st.setString( 4, c.getType().getValue() );
		st.setInt( 5, c.getType().ordinal() );
		st.executeUpdate();
		ResultSet rs = st.getGeneratedKeys();
		/*PreparedStatement st2 = connection.prepareStatement(ORACLE_INSERTED_ID_QUERY);
		ResultSet rs2 = st2.executeQuery();*/
		
		if ( rs.next() ) {
			c.setId( rs.getString( 1 ) );
		}
	}
	
	@Override
	public Contact findById( Long aLong ) throws SQLException {

        Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( FIND_BY_ID_QUERY, Statement.RETURN_GENERATED_KEYS );
        st.setString( 1, aLong.toString() );
        ResultSet rs =st.executeQuery();
        while (rs.next ()){
            Contact c = new Contact();
            c.setId(rs.getString(1));
            c.setName(rs.getString(2));
            c.setEmail(rs.getString(3));
            c.setPhone(rs.getString(4));
            if (rs.getString(6) == Contact.Type.PERSO.toString()) {
                c.setType(Contact.Type.PERSO);
            } else {
                c.setType(Contact.Type.PRO);
            }
            return c;
        }
        return null;
	}
	
	@Override
	public List<Contact> findAll() throws SQLException {
        Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( FIND_ALL_QUERY);
        ResultSet v =st.executeQuery();
        List<Contact> contactList = new ArrayList<>();
        while (v.next ()) {

            Contact c = new Contact();
            c.setId(v.getString(1));
            c.setName(v.getString(2));
            c.setEmail(v.getString(6));
            c.setPhone(v.getString(3));
            if (v.getString(4) == Contact.Type.PERSO.toString()) {
                c.setType(Contact.Type.PERSO);
            } else {
                c.setType(Contact.Type.PRO);
            }
            contactList.add(c);
        }

        return contactList;
	}
	
	@Override
	public Contact update( Contact o ) throws SQLException {

    Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS );
        st.setString( 1, o.getName() );
        st.setString( 2, o.getEmail() );
        st.setString( 3, o.getPhone() );
        st.setString( 4, o.getType().getValue() );
        st.setInt( 5, o.getType().ordinal() );
        st.setString( 6, o.getId() );
        st.executeUpdate();
        return null;

	}
	
	@Override
	public void remove( Contact o ) throws SQLException {
        Connection connection = PersistenceManager.getConnection();
        PreparedStatement st = connection.prepareStatement( REMOVE_QUERY, Statement.RETURN_GENERATED_KEYS );
        st.setString( 1, o.getId() );
        st.executeUpdate();
        ResultSet rs = st.getGeneratedKeys();
	}
}


