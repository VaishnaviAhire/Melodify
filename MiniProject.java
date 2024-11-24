import java.util.*;
import java.sql.*;

class Melodify {
	Connection con = null;
	PreparedStatement pstmt = null;
	String query;
	Scanner sc = new Scanner(System.in);
	Scanner sc_str = new Scanner(System.in).useDelimiter("\n");
	
	void createPlayList(int userID) {
		query = "INSERT INTO Playlist (UserID, PlaylistName) VALUES (?, ?)";

		System.out.print("Enter name of playlist: ");
		String name = sc_str.next();

		try {
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, userID);
			pstmt.setString(2, name);
			pstmt.executeUpdate();
			System.out.println("Playlist created successfully!");
		}

		catch (SQLException e) {
			System.out.println("Error while creating playlist: " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	void addSongstoPlaylist(int playlistID) {
		String query = "INSERT INTO Playlist_Contains_Song VALUES (?, ?)";
		String query1 = "SELECT GetSongidBySong(Title) FROM Song WHERE Title = ?";

		System.out.print("Enter title of the song you want to add: ");
		String s_title = sc_str.next();

		try {
			pstmt = con.prepareStatement(query1);
			pstmt.setString(1, s_title);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int s_id = rs.getInt(1);

				pstmt = con.prepareStatement(query);
				pstmt.setInt(1, playlistID);
				pstmt.setInt(2, s_id);

				pstmt.executeUpdate();
				System.out.println("Song added to the playlist successfully!");
			} else {
				System.out.println("Song does not exist!");
			}

			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			System.out.println("Error while adding song to playlist: " + e.getMessage());
			e.printStackTrace();
		}
	}

	void likeSong(int userID) {
		query = "INSERT INTO User_Likes_Song VALUES (?, ?)";
		String query1 = "SELECT GetSongidBySong(Title) FROM Song WHERE Title = ?";

		System.out.print("Enter title of the song you want to like: ");
		String s_title = sc_str.next();

		try {
			pstmt = con.prepareStatement(query1);
			pstmt.setString(1, s_title);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int s_id = rs.getInt(1);

				pstmt = con.prepareStatement(query);
				pstmt.setInt(1, userID);
				pstmt.setInt(2, s_id);

				pstmt.executeUpdate();
				System.out.println("Song liked!");
			} else {
				System.out.println("Song does not exist!");
			}

			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			System.out.println("Error while liking song to playlist: " + e.getMessage());
			e.printStackTrace();
		}
	}

	void showPlaylist(int userID) {
		query = "SELECT PlaylistName FROM Playlist WHERE UserID = ?";

		try {
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, userID);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String name = rs.getNString(1);
				System.out.println(name);
			}
		} catch (SQLException e) {
			System.out.println("Error while showing playlist: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	void showSongs(int userID) {
		query = "SELECT GetSongfromSongID(SongID) FROM Playlist NATURAL JOIN Playlist_Contains_Song WHERE UserID = ? and PlaylistName = ?";
		System.out.print("Enter playlist name: ");
		String name = sc_str.next();

		try {
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, userID);
			pstmt.setString(2, name);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String song = rs.getNString(1);
				System.out.println(song);
			}
		} catch (SQLException e) {
			System.out.println("Error while showing songs: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	void searchSong() {
		query = "Select * from Song where Title = ?";
		String query1 = "Select Album.Title from Song join Album where Song.AlbumID = Album.AlbumID and Song.Title = ?";
		String query2 = "Select GenreName from Song join Genre where Song.GenreID = Genre.GenreID and Title = ?";

		System.out.print("Enter title of song: ");
		String title = sc_str.next();

		try {
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, title);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String songTitle = rs.getNString(2);
				String duration = rs.getTime(3).toString();
				String releaseDate = rs.getDate(4).toString();
				int albumID = rs.getInt(5);
				int genreID = rs.getInt(6);

				System.out.println("Song found!");
				System.out.println("Song title: " + songTitle);

				PreparedStatement pstmt1 = con.prepareStatement(query1);
				pstmt1.setInt(1, albumID);
				ResultSet rs1 = pstmt1.executeQuery();
				if (rs1.next()) {
					String albumName = rs1.getNString(1);
					System.out.println("Album name: " + albumName);
				}

				PreparedStatement pstmt2 = con.prepareStatement(query2);
				pstmt2.setInt(1, genreID);
				ResultSet rs2 = pstmt2.executeQuery();
				if (rs2.next()) {
					String genreName = rs2.getNString(1);
					System.out.println("Genre: " + genreName);
				}

				System.out.println("Song duration: " + duration);
				System.out.println("Release date: " + releaseDate);
			} else {
				System.out.print("Song not found!");
			}

		} catch (SQLException e) {
			System.out.println("Error while searching song from playlist: " + e.getMessage());
			e.printStackTrace();
		}
	}

	void createAccount() {
		query = "INSERT INTO User (Username, Email, Password) VALUES (?, ?, ?)";

		System.out.print("Enter username: ");
		String name = sc_str.next();
		System.out.print("Enter email: ");
		String emailid = sc_str.next();
		System.out.print("Set password: ");
		String pd = sc_str.next();

		try {
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, name);
			pstmt.setString(2, emailid);
			pstmt.setString(3, pd);

			pstmt.executeUpdate();
			System.out.println("Account created successfully!");
			System.out.println();
			login();
		} catch (SQLException e) {
			System.out.println("Error while creating account: " + e.getMessage());
			e.printStackTrace();
		}
	}

	void login() {
		String query = "SELECT * FROM User WHERE Email = ?";
		System.out.print("Enter email: ");
		String email = sc_str.next();
		System.out.print("Enter password: ");
		String pd = sc_str.next();

		try {
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, email);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String correct_pd = rs.getString(4);
				int userID = rs.getInt(1);

				if (pd.equals(correct_pd)) {
					System.out.println("You have logged in successfully!");
					while (true) {
						System.out.println();
						System.out.print(
								"Menu:\n1: Create Playlist\n2: Add songs to playlist \n3: Like a song\n4: Show playlists\n5: Show songs from a playlist\n6: Search a song\n7: Exit\nChoose an option: ");
						int ch = sc.nextInt();
						System.out.println();

						switch (ch) {
						case 1:
							createPlayList(userID);
							break;

						case 2:
							query = "select PlaylistID from Playlist where PlaylistName = ? and UserID = ?";
							pstmt = con.prepareStatement(query);
							System.out.print("Enter playlist name: ");
							String name = sc_str.next();
							pstmt.setString(1, name);
							pstmt.setInt(2, userID);
							ResultSet rs1 = pstmt.executeQuery();
							if (rs1.next()) {
								int playlistID = rs1.getInt(1);
								addSongstoPlaylist(playlistID);
							}
							rs1.close();
							break;

						case 3:
							query = "select PlaylistID from Playlist where UserID = ?";
							pstmt = con.prepareStatement(query);
							pstmt.setInt(1, userID);
							ResultSet rs2 = pstmt.executeQuery();
							if (rs2.next()) {
								int playlistID = rs2.getInt(1);
								likeSong(playlistID);
							}
							rs2.close();
							break;

						case 4:
							showPlaylist(userID);
							break;

						case 5:
							showSongs(userID);
							break;

						case 6:
							searchSong();
							break;

						case 7:
							System.exit(0);
						}
					}
				} else {
					System.out.print("Incorrect password!");
				}
			} else {
				System.out.println("Account does not exist!");
				System.out.print("Do you want to create an account? 1 for yes and 0 for no: ");
				int choice = sc.nextInt();
				if (choice == 1) {
					createAccount();
				}
			}

			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			System.out.println("Error while logging in: " + e.getMessage());
			e.printStackTrace();
		}

	}

}

public class MiniProject {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		Melodify ob = new Melodify();
		Scanner sc = new Scanner(System.in);

		Class.forName("com.mysql.cj.jdbc.Driver");
		ob.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/melodify", "root", "Sawan");
		
		System.out.println("Welcome to Melodify!");
		
		System.out.println();
		
		System.out.print("1: Create an Account\n2: Login\nChoose an option: ");
		int ch = sc.nextInt();
		
		if (ch == 1) {
			ob.createAccount();
		}
		
		else if (ch == 2) {
			ob.login();
		}
	}

}
