import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class ebayMain 
{
	private Connection connection;
	private Statement statement;
	private ResultSet resultset;
	private String username;
	private String password;
    public static void main(String [] args)
    {
    	ebayMain e = new ebayMain();
    	e.mainScreen();
    }
   
    public int mainScreen()
    {
        int login;
        BufferedReader dataIn = new BufferedReader( new    InputStreamReader(System.in) );
        
        System.out.println("Welcome to the CS1555 Ebay knockoff. Would you like to sign in as Customer(0) or Administrator(1)");
        System.out.print(">>");
        try{ login = Integer.parseInt(dataIn.readLine()); }
        catch(Exception e){ login = 0; }
        
        System.out.print("Username: ");
        try{ username = dataIn.readLine();}
        catch(IOException e){ username=""; }
        
        System.out.print("Password: ");
        try{ password = dataIn.readLine(); }
        catch(IOException e){ password=""; }   
        
        try
        {
        	DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
        	connection = DriverManager.getConnection("jdbc:oracle:thin:@db10.cs.pitt.edu:1521:dbclass", "mjb162", "3580903");
        	statement = connection.createStatement();
        	if(login == 0)
        		resultset = statement.executeQuery("select * from Customer where login = '" + username + "'");
        	else
        		resultset = statement.executeQuery("select * from Administrator where login = '" + username + 
        				"' and password = '" + password + "'");
        	if(!resultset.next())
        	{
        		System.out.println("Login failed");
        		connection.close();
        		return -2;
        	}
        	else
        		System.out.println("Login Successful");
        	Thread.sleep(4000);
        }
        catch(Exception e)
        {
        	System.out.println(e);
        	e.printStackTrace();
        	try{ connection.close(); }
			catch(Exception f) { return -3; }
        	return -1;
        }
        clearScreen();
        
        if(login == 0)
        	customerInterface();
        else
        	adminInterface();
        try{ connection.close(); }
		catch(Exception g) { return -3; }
        return 0;
    }
   
    public int customerInterface()
    {
    	BufferedReader dataIn = new BufferedReader( new InputStreamReader(System.in));
    	int data;
    	while(true)
    	{
	    	System.out.println("Customer Main Menu");
	    	System.out.println("1.) Browse For Products");
	    	System.out.println("2.) Search For Product By Text");
	    	System.out.println("3.) Auction Products");
	    	System.out.println("4.) Bidding on Products");
	    	System.out.println("5.) Selling Products");
	    	System.out.println("6.) Suggestions");
	    	System.out.println("7.) Log out");
	    	System.out.print(">> ");
	    	try{ data = Integer.parseInt(dataIn.readLine()); }
	    	catch(Exception e) { System.out.println("Invalid Input"); continue; }
	    	
	    	switch(data)
	    	{
	    		case 1:
	    			browseProduct();
	    			break;
	    		case 2:
	    			searchProduct();
	    			break;
	    		case 3:
	    			auctionProduct();
	    			break;
	    		case 4:
	    			bidProduct();
	    			break;
	    		case 5:
	    			sellProduct();
	    			break;
	    		case 6:
	    			suggestion();
	    			break;
	    		case 7:
	    			try{ connection.close(); }
	    			catch(Exception e) { return -3; }
	    			return 0;
	    		default:
	    			continue;
	    	}
    	}
	    	
    }
    
    public int browseProduct()
    {
    	BufferedReader dataIn = new BufferedReader( new InputStreamReader(System.in));
    	String currentcatagory = "";	//Root position
    	try
    	{
    		statement = connection.createStatement();
    		resultset = statement.executeQuery("select * from Catalog where Catalog.parent_catalog is NULL"); 	//Returns root entries
    		while(resultset.next())
    		{
    			System.out.println(resultset.getString(1));
    		}
    		while(true)
    		{
    			System.out.print("Select catagory name: ");
    			currentcatagory = dataIn.readLine();
    			statement = connection.createStatement();
    			resultset = statement.executeQuery("select * from Catalog where Catalog.parent_catalog = '" + currentcatagory + "'");
    			if(resultset.next())	//If this is not a leaf, keep selecting categories
    			{
    				do
    				{
    					System.out.println(resultset.getString(1));
    				}while(resultset.next());
    				continue;
    			}
    			else	//leaf category, print results
    			{
    				System.out.println(currentcatagory);
    				statement = connection.createStatement();
    				resultset = statement.executeQuery("select * from Product, BelongsTo, Catalog where BelongsTo.catalog = catalog.name and BelongsTo.auction_id = Product.auction_id and catalog.name = '" +
    						currentcatagory + "'");
    				System.out.println("Auction-ID\tName\tDescription\tSeller\tStart Date\tMin Price\tNumber of Days\tStatus\tBuyer\tSell Date\tAmount");
    				while(resultset.next())
    				{
    					System.out.println(resultset.getInt(1) + "\t" + resultset.getString(2) + "\t" + resultset.getString(3) + "\t" +
    							resultset.getString(4) + "\t" + resultset.getDate(5) + "\t" + resultset.getInt(6) + "\t" + resultset.getInt(7) + "\t" + 
    							resultset.getString(8) + "\t" + resultset.getString(9) + "\t" + resultset.getDate(10) + "\t" + resultset.getInt(11)); 
    				}
    				break;
    			}
    		}
    	}
    	catch(Exception e)
    	{ 
    		e.printStackTrace();
    		return -1;
    	}
    	return 0;
    }
    
    public int searchProduct()
    {
    	BufferedReader dataIn = new BufferedReader( new InputStreamReader(System.in));
    	String description;
    	String[] pieces;
    	try
    	{
    		while(true)
    		{
    			System.out.print("Please enter two words to search on: ");
	    		description = dataIn.readLine();
		    	statement = connection.createStatement();
		    	pieces = description.split(" ");
		    	if(pieces.length == 2)
		    	{
		    		resultset = statement.executeQuery("select * from Product where Product.description like '%" + 
		    				pieces[0] + "%' AND Product.description like '%" + pieces[1] + "%'");
		    	}
		    	else if(pieces.length == 1)
		    	{
		    		resultset = statement.executeQuery("select * from Product where Product.description like '%" + 
		    				pieces[0] + "%'");
		    	}
		    	else
		    	{
		    		System.out.println("Error: Please enter > 0 words but < 2 words to search for");
		    		continue;
		    	}
		    	if(resultset.next())	//We have at least one hit
		    		break;
		    	else
		    	{
		    		System.out.println("Sorry, no items met your search requirements, exiting...");
		    		return 0;
		    	}
    		}
    		do 
			{
				System.out.println(resultset.getInt(1) + "\t" + resultset.getString(2) + "\t" + resultset.getString(3) + "\t" +
						resultset.getString(4) + "\t" + resultset.getDate(5) + "\t" + resultset.getInt(6) + "\t" + resultset.getInt(7) + "\t" + 
						resultset.getString(8) + "\t" + resultset.getString(9) + "\t" + resultset.getDate(10) + "\t" + resultset.getInt(11)); 
			}while(resultset.next());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return -1;
    	}
    	return 0;
    }
   
    public int auctionProduct()
    {
    	String name, description, price, days;
    	String[] catagories;
    	BufferedReader dataIn = new BufferedReader( new InputStreamReader(System.in));
    	
    	try
    	{
	    	System.out.print("Please enter the product name: ");
	    	name = dataIn.readLine();
	    	System.out.print("Would you like to enter an description (y/n): ");
	    	if(dataIn.readLine().equals("y"))
	    	{
	    		System.out.print("Please enter a description: ");
	    		description = dataIn.readLine();
	    	}
	    	else
	    		description = "";
	    	System.out.print("Please enter the minimum price: ");
	    	price = dataIn.readLine();
	    	System.out.print("Please enter a comma seperate list of catagories: ");
	    	catagories = dataIn.readLine().split(",");
	    	System.out.print("Please enter number of days up for auction: ");
	    	days = dataIn.readLine();
	    	CallableStatement cs = connection.prepareCall("{call put_product(?, ?, ?, ?, ?, ?) }");
	    	cs.setString(1, name);
	    	cs.setString(2, description);
	    	cs.setString(3, username);
	    	cs.setInt(4, Integer.parseInt(price));
	    	cs.setInt(5, Integer.parseInt(days));
	    	cs.registerOutParameter(6, java.sql.Types.INTEGER);
	    	cs.executeQuery();
	    	int auction_id = cs.getInt(6);
	    	for(int i=0; i<catagories.length; i++)
	    	{
		    	CallableStatement cs2 = connection.prepareCall("{call put_BelongsTo(?, ?) }");
	    		cs2.setInt(1, auction_id);
	    		cs2.setString(2, catagories[i]);
	    		cs2.executeQuery();
	    	}
	    	System.out.println("Insert Successful");
	    	Thread.sleep(2000);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return -1;
    	}
    	return 0;
    }
   
    public int bidProduct()
    {
    	int auction_id, bid, min_bid;
    	BufferedReader dataIn = new BufferedReader(new InputStreamReader(System.in));
    	try
    	{
	    	System.out.print("Please enter auction id of item to bid on: ");
	    	auction_id = Integer.parseInt(dataIn.readLine());
	    	connection.setAutoCommit(false);
	    	statement = connection.createStatement();
	    	statement.execute("lock table Bidlog in exclusive mode");
	    	statement.execute("lock table Product in exclusive mode");
	    	resultset = statement.executeQuery("select * from Product where status = 'underauction' and auction_id =" + auction_id);
	    	if(!resultset.next())
	    	{
	    		System.out.println("Product is either closed, sold, or not in the database");
	    		connection.rollback();
	    		return -5;
	    	}
	    	min_bid = resultset.getInt(6);
	    	resultset = statement.executeQuery("select * from (select * from Bidlog where auction_id =" + 
	    			auction_id + " order by amount DESC) where rownum = 1 order by rownum");
	    	if(resultset.next())
	    		min_bid = resultset.getInt(5);
	    	System.out.print("Please enter a value > " + min_bid + ": ");
	    	bid = Integer.parseInt(dataIn.readLine());
	    	if(bid < min_bid)
	    	{
	    		System.out.println("Error, bid less than minimum");
	    		connection.rollback();
	    		return -2;
	    	}
	    	statement.executeUpdate("insert into Bidlog values(bidsn_autoincrement.NEXTVAL, " + auction_id + ", '" + 
	    			username + "', (select c_date from ourdate), " + bid + ")");
	    	connection.commit();
    	}
    	catch(Exception e)
    	{
    		try{ connection.rollback(); }
    		catch(Exception f)
    		{ f.printStackTrace(); return -4; }
    		e.printStackTrace();
    		return -1;
    	}
    	finally
    	{
    		try{ connection.setAutoCommit(true); }
    		catch(Exception e)
    		{ e.printStackTrace(); return -3; }
    	}
    	System.out.println("Update successful");
    	return 0;
    }

    public int sellProduct()
    {
    	int auction_id;
    	BufferedReader dataIn = new BufferedReader( new InputStreamReader(System.in) );
    	try
    	{
    		Statement s1 = connection.createStatement();
    		Statement s2 = connection.createStatement();
    		statement = connection.createStatement(resultset.TYPE_SCROLL_INSENSITIVE, resultset.CONCUR_UPDATABLE);
    		//select all products which were bided on, closed, and seller is the logged in user
    		resultset = statement.executeQuery("select * from product where amount is not null and status = 'closed' and seller = '" + username +"'");
    		if(!resultset.next())
    		{
    			System.out.println("You have no closed sold products");
    			Thread.sleep(2000);
    			return -2;
    		}
    		do
    		{
    			System.out.println("List of your sold products: (AuctionID, Name, Description, Start Date, Minimum Price, Number of Days, Status)");
    			System.out.println(resultset.getInt(1) + "\t" + resultset.getString(2) + "\t" + 
    					resultset.getString(3) + "\t" + resultset.getDate(5) + "\t" +
    					resultset.getInt(6) + "\t" + resultset.getInt(7) + "\t" +
    					resultset.getString(8));
    		}while(resultset.next());
    
    		System.out.print("Please enter auction id of auction to sell: ");
    		auction_id = Integer.parseInt(dataIn.readLine());
    		resultset.beforeFirst();
    		
    		while(resultset.next())
    		{
    			if(resultset.getInt(1) == auction_id)
    			{
    				//Select second price
    				ResultSet secondbid, topbid;
    				topbid = s1.executeQuery("select * from ( select * from bidlog where auction_id = " + auction_id + " order by amount desc ) where rownum = 1 order by rownum");
    				secondbid = s2.executeQuery("select * from ( select a.*, rownum rnum from ( select * from bidlog where auction_id = " + auction_id + " order by amount desc) a where rownum <= 2) where rnum >= 2");
    				if(topbid.next() && secondbid.next())
    					statement.executeUpdate("update product set status = 'sold', buyer = '" + topbid.getString(3) + "', sell_date = (select c_date from ourdate), amount = " + secondbid.getInt(5) + " where auction_id = " + auction_id);
    				else
    					statement.executeUpdate("update product set status = 'sold', buyer = '" + topbid.getString(3) + "', sell_date = (select c_date from ourdate), amount = " + topbid.getInt(5) + " where auction_id = " + auction_id);
    				System.out.println("Update successful");
    				s1.close();
    				s2.close();
    				return 0;
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return -1;
    	}
    	return 0;
    }
    public int suggestion()
    {
    	try
    	{
    		statement = connection.createStatement();
    		resultset = statement.executeQuery(
    				"select auction_id, count(bidder) count from (select distinct b.auction_id, b.bidder from bidlog b, (select distinct bidder from bidlog where auction_id in (select distinct auction_id from bidlog where bidder = '" + 
    				username + "' ) and bidder != '" + username + "' ) c where b.bidder = c.bidder) group by auction_id order by count desc");
    		System.out.println("Check out these auctions:");
    		System.out.println("AuctionID\tRelavence");
    		while(resultset.next())
    			System.out.println(resultset.getInt(1) + "\t\t" + resultset.getInt(2));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return -1;
    	}
    	return 0;
    }
   
    
    public int adminInterface()
    {
    	while(true)
    	{
	    	int data = -1;
	    	System.out.println("Admin Main Menu");
	    	System.out.println("1.) Register new users");
	    	System.out.println("2.) Update System Time");
	    	System.out.println("3.) Product Statistics");
	    	System.out.println("4.) Statistics");
	    	System.out.println("5.) Logout");
	    	
	    	System.out.print(">> ");
	    	BufferedReader dataIn = new BufferedReader( new InputStreamReader(System.in));
	    	try{ data = Integer.parseInt(dataIn.readLine()); }
	    	catch(Exception e) { System.out.println("Invalid Input"); }
	    	
	    	switch(data)
	    	{
	    		case 1:
	    			registerCustomer();
	    			break;
	    		case 2:
	    			updateTime();
	    			break;
	    		case 3:
	    			productStatistics();
	    			break;
	    		case 4:
	    			statistics();
	    			break;
	    		case 5:
	    			try{ connection.close(); }
	    			catch(Exception e) { return -3; }
	    			return 0;
	    		default:
	    	}
    	}
    }
    
    
    public void registerCustomer()
    {
        String password, name, address, email, login, administrator;
        clearScreen();
        BufferedReader dataIn = new BufferedReader( new InputStreamReader(System.in));
        System.out.println("Registering new customer!\n");
        System.out.println("Their name?");
        System.out.print(">>");
        try
        {
            name = dataIn.readLine();
            System.out.println("Address?");
            System.out.print(">>");
            address = dataIn.readLine();
            System.out.println("Email address?");
            System.out.print(">>");
            email = dataIn.readLine();
            System.out.println("login?");
            System.out.print(">>");
            login = dataIn.readLine();
            System.out.println("And their password?");
            System.out.print(">>");
            password = dataIn.readLine();
            System.out.println("Are they an admin?(y/n)");
            System.out.print(">>");
            administrator = dataIn.readLine();
            String query;
            if(administrator.equals("y"))
            {
                query = "INSERT INTO administrator (login, password, name, address, email) values('"+ login + "', '" + password + "', '" + name + "', '" + address + "', '" + email + "')";
            }
            else
            {
                query = "INSERT INTO customer (login, password, name, address, email) values('"+ login + "', '" + password + "', '" + name + "', '" + address + "', '" + email + "')";
            }
            statement = connection.createStatement();
            int result = statement.executeUpdate(query);
            if(result != 0)
            {
                System.out.println("Registered user successfully.");
            }
            else
            {
                System.out.println("Register failed.");
            }
            Thread.sleep(2000);
            clearScreen();
	    }
	    catch(Exception e){System.out.println("Failure...back to admin interface");}
	    adminInterface();
    }
    
    
    public void updateTime()
    {    
        String query = "UPDATE ourdate SET c_date = to_date('";
        String input;
        clearScreen();
           BufferedReader dataIn = new BufferedReader( new InputStreamReader(System.in));
        System.out.println("Input the new time in a 'DD-MM-YY/HH:MI:SSAM' format.");
        System.out.print(">>");
        try
        {
            input = dataIn.readLine();
            query += "" + input +"', 'DD-MM-YY/HH:MI:SSAM')";
            statement = connection.createStatement();
            int result = statement.executeUpdate(query);
               if(result != 0)
               {
                   System.out.println("Updated time successfully.");
               }
               else
               {
                   System.out.println("Update failed.");
               }
            Thread.sleep(2000);
            clearScreen();
        }
        catch(Exception e){System.out.println("Failure...back to admin interface");}
        adminInterface();
    }
   
    
    public void productStatistics()
    {
        String seller = "";
        int data = 0;
        clearScreen();
        System.out.println("Product Statistics");
        System.out.println("1.) Stats for all products");
        System.out.println("2.) Stats by user");
        System.out.println("3.) Back to the admin screen");
        System.out.print(">> ");
        BufferedReader dataIn = new BufferedReader( new InputStreamReader(System.in));
        String query1 = "select product.name, product.status, product.amount, product.buyer, bidlog.bidder from product left outer join bidlog on product.auction_id = bidlog.auction_id where (bidlog.amount = product.amount or bidlog.bidder is null)";
        try{ data = Integer.parseInt(dataIn.readLine()); }
        catch(Exception e) { System.out.println("Invalid Input"); }

        switch(data)
        {
            case 1:
                
                break;
            case 2:
                System.out.println("Who's the seller?");
                System.out.print(">>");
                try{ seller = dataIn.readLine(); }
                catch(Exception e) { System.out.println("Invalid Input"); }
                query1 +=" AND product.seller = '" + seller + "'";
                break;
            case 3:
                adminInterface();
                break;
            default:
        }
        try
        {
            statement = connection.createStatement();
            resultset = statement.executeQuery(query1);
            System.out.println("Products by order of name, status, amount, buyer/bidder");
            while(resultset.next())
            {
                 System.out.print(resultset.getString(1) + "\t");
                 System.out.print(resultset.getString(2) + "\t");
                 if(resultset.getString(4) != null)
                 {
                     System.out.print( "" + resultset.getInt(3) + "\t");
                     System.out.println(resultset.getString(4) + ": buyer");
                 }
                 else if(resultset.getString(5) != null)
                 {
                     System.out.print( "" + resultset.getInt(3) + "\t");
                     System.out.println(resultset.getString(5) + ": high bidder");
                 }
                 else
                 {
                	 System.out.println("no bids");
                 }

            }
        	System.out.println("hit enter when ready to go back");
        	dataIn.readLine();  
        	clearScreen();
        	adminInterface();
        }
        catch(Exception e){clearScreen(); e.printStackTrace();}
        adminInterface();
    }

    
    public void statistics()
    {
    	ResultSet resultset2, resultset3, resultset4;
    	String query1, query2, query3, query4;
    	int x = 0;
    	int k=0;
    	BufferedReader dataIn = new BufferedReader( new InputStreamReader(System.in));
    	clearScreen();
    	System.out.println("How many months back do you want the statistics to go?");
    	System.out.print(">>");
    	try
    	{
      		x = Integer.parseInt(dataIn.readLine());
    		System.out.println("and the top how many?");
    		System.out.print(">>");
    		k = Integer.parseInt(dataIn.readLine());
    	}
        catch(Exception e) { System.out.println("Invalid Input"); }
        query1 = "SELECT * FROM (SELECT name, Product_Count(" + x + ", name) as total FROM catalog WHERE parent_catalog IS NULL ORDER BY total DESC) WHERE rownum <= " + k + " order by rownum asc";
        query2 = "SELECT * FROM (SELECT name, Product_Count(" + x + ", name) as total FROM catalog WHERE name not in parent_catalog ORDER BY total DESC) WHERE rownum <= " + k + "";
        query3 = "SELECT * FROM(SELECT DISTINCT bidder, Bid_Count(bidder, " + x + ") as total from bidlog order by total desc) where rownum <= " + k + " order by rownum asc";
        query4 = "SELECT * FROM(SELECT DISTINCT buyer, Buying_Amount(buyer, " + x + ") as total from product where buyer is not null order by total desc) where rownum <=" + k + " order by rownum asc";
        try
        {
        	statement = connection.createStatement();
        	resultset = statement.executeQuery(query1);
        	statement = connection.createStatement();
        	resultset2 = statement.executeQuery(query2);
        	statement = connection.createStatement();
        	resultset3 = statement.executeQuery(query3);
        	statement = connection.createStatement();
        	resultset4 = statement.executeQuery(query4);
        	
        	System.out.println("TOP " + k + " SELLING ROOT NODE CATEGORIES\n");
        	while(resultset.next())
        	{
        		System.out.print(resultset.getString(1));
        		System.out.print("\t");
        		System.out.println(resultset.getInt(2));
        		System.out.println("\n");
        	}
        	System.out.println("TOP " + k + " SELLING LEAF NODE CATEGORIES");
        	while(resultset2.next())
        	{
        		System.out.print(resultset2.getString(1));
        		System.out.print("\t");
        		System.out.println(resultset2.getInt(2));
        		System.out.println("\n");
        	}
        	System.out.println("TOP " + k + " ACTIVE BIDDERS");
        	while(resultset3.next())
        	{
        		System.out.print(resultset3.getString(1));
        		System.out.print("\t");
        		System.out.println(resultset3.getInt(2));
        		System.out.println("\n");
        	}
        	System.out.println("TOP " + k + " ACTIVE BUYERS");
        	while(resultset4.next())
        	{
        		System.out.print(resultset4.getString(1));
        		System.out.print("\t");
        		System.out.println(resultset4.getInt(2));
        		System.out.println("\n");
        	}
        	System.out.println("hit enter when ready to go back");
        	k = Integer.parseInt(dataIn.readLine());  
        	clearScreen();
        	adminInterface();
        }
        catch(Exception e){clearScreen(); e.printStackTrace(); System.out.println("Getting the statistics failed."); adminInterface();}
    }

    public void clearScreen()
    {
    	for(int i=0; i<50; i++)
    		System.out.print("\n");
    }
}

