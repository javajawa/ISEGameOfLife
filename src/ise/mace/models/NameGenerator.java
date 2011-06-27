package ise.mace.models;

import java.util.Random;

/**
 *
 */
public class NameGenerator
{

	private static class SortUtil
	{

		static void sort(String[] values)
		{
			int i;
			String temp;
			for (i = 0; i < values.length; i++)
			{
				int r = (rand.nextInt() & 0x7FFFFFFF) % values.length;
				temp = values[i];
				values[i] = values[r];
				values[r] = temp;
			}
		}

		private SortUtil()
		{
			// Nothing to see here. Move along, citizen.
		}
	}

	private static String[] foreNames =
	{
		"James", "John", "Robert", "Michael", "William", "David", "Richard",
		"Charles", "Joseph", "Thomas", "Christopher", "Daniel", "Paul", "Mark",
		"Donald", "George", "Kenneth", "Steven", "Edward", "Brian", "Ronald",
		"Anthony", "Kevin", "Jason", "Matthew", "Gary", "Timothy", "Jose", "Larry",
		"Jeffrey", "Frank", "Scott", "Eric", "Stephen", "Andrew", "Raymond",
		"Gregory", "Joshua", "Jerry", "Dennis", "Walter", "Patrick", "Peter",
		"Harold", "Douglas", "Henry", "Carl", "Arthur", "Ryan", "Roger", "Joe",
		"Juan", "Jack", "Albert", "Jonathan", "Justin", "Terry", "Gerald", "Keith",
		"Samuel", "Willie", "Ralph", "Lawrence", "Nicholas", "Roy", "Benjamin",
		"Bruce", "Brandon", "Adam", "Harry", "Fred", "Wayne", "Billy", "Steve",
		"Louis", "Jeremy", "Aaron", "Randy", "Howard", "Eugene", "Carlos", "Russell",
		"Bobby", "Victor", "Martin", "Ernest", "Phillip", "Todd", "Jesse", "Craig",
		"Alan", "Shawn", "Clarence", "Sean", "Philip", "Chris", "Johnny", "Earl",
		"Jimmy", "Antonio", "Danny", "Bryan", "Tony", "Luis", "Mike", "Stanley",
		"Leonard", "Nathan", "Dale", "Manuel", "Rodney", "Curtis", "Norman", "Allen",
		"Marvin", "Vincent", "Glenn", "Jeffery", "Travis", "Jeff", "Chad", "Jacob",
		"Lee", "Melvin", "Alfred", "Kyle", "Francis", "Bradley", "Jesus", "Herbert",
		"Frederick", "Ray", "Joel", "Edwin", "Don", "Eddie", "Ricky", "Troy",
		"Randall", "Barry", "Alexander", "Bernard", "Mario", "Leroy", "Francisco",
		"Marcus", "Micheal", "Theodore", "Clifford", "Miguel", "Oscar", "Jay", "Jim",
		"Tom", "Calvin", "Alex", "Jon", "Ronnie", "Bill", "Lloyd", "Tommy", "Leon",
		"Derek", "Warren", "Darrell", "Jerome", "Floyd", "Leo", "Alvin", "Tim",
		"Wesley", "Gordon", "Dean", "Greg", "Jorge", "Dustin", "Pedro", "Derrick",
		"Dan", "Lewis", "Zachary", "Corey", "Herman", "Maurice", "Vernon", "Roberto",
		"Clyde", "Glen", "Hector", "Shane", "Ricardo", "Sam", "Rick", "Lester",
		"Brent", "Ramon", "Charlie", "Tyler", "Gilbert", "Gene", "Marc", "Reginald",
		"Ruben", "Brett", "Angel", "Nathaniel", "Rafael", "Leslie", "Edgar",
		"Milton", "Raul", "Ben", "Chester", "Cecil", "Duane", "Franklin", "Andre",
		"Elmer", "Brad", "Gabriel", "Ron", "Mitchell", "Roland", "Arnold", "Harvey",
		"Jared", "Adrian", "Karl", "Cory", "Claude", "Erik", "Darryl", "Jamie",
		"Neil", "Jessie", "Christian", "Javier", "Fernando", "Clinton", "Ted",
		"Mathew", "Tyrone", "Darren", "Lonnie", "Lance", "Cody", "Julio", "Kelly",
		"Kurt", "Allan", "Nelson", "Guy", "Clayton", "Hugh", "Max", "Dwayne",
		"Dwight", "Armando", "Felix", "Jimmie", "Everett", "Jordan", "Ian",
		"Wallace", "Ken", "Bob", "Jaime", "Casey", "Alfredo", "Alberto", "Dave",
		"Ivan", "Johnnie", "Sidney", "Byron", "Julian", "Isaac", "Morris", "Clifton",
		"Willard", "Daryl", "Ross", "Virgil", "Andy", "Marshall", "Salvador",
		"Perry", "Kirk", "Sergio", "Marion", "Tracy", "Seth", "Kent", "Terrance",
		"Rene", "Eduardo", "Terrence", "Enrique", "Freddie", "Wade", "Mary",
		"Patricia", "Linda", "Barbara", "Elizabeth", "Jennifer", "Maria", "Susan",
		"Margaret", "Dorothy", "Lisa", "Nancy", "Karen", "Betty", "Helen", "Sandra",
		"Donna", "Carol", "Ruth", "Sharon", "Michelle", "Laura", "Sarah", "Kimberly",
		"Deborah", "Jessica", "Shirley", "Cynthia", "Angela", "Melissa", "Brenda",
		"Amy", "Anna", "Rebecca", "Virginia", "Kathleen", "Pamela", "Martha",
		"Debra", "Amanda", "Stephanie", "Carolyn", "Christine", "Marie", "Janet",
		"Catherine", "Frances", "Ann", "Joyce", "Diane", "Alice", "Julie", "Heather",
		"Teresa", "Doris", "Gloria", "Evelyn", "Jean", "Cheryl", "Mildred",
		"Katherine", "Joan", "Ashley", "Judith", "Rose", "Janice", "Nicole", "Judy",
		"Christina", "Kathy", "Theresa", "Beverly", "Denise", "Tammy", "Irene",
		"Jane", "Lori", "Rachel", "Marilyn", "Andrea", "Kathryn", "Louise", "Sara",
		"Anne", "Jacqueline", "Wanda", "Bonnie", "Julia", "Ruby", "Lois", "Tina",
		"Phyllis", "Norma", "Paula", "Diana", "Annie", "Lillian", "Emily", "Robin",
		"Peggy", "Crystal", "Gladys", "Rita", "Dawn", "Connie", "Florence", "Edna",
		"Tiffany", "Carmen", "Rosa", "Cindy", "Grace", "Wendy", "Victoria", "Edith",
		"Kim", "Sherry", "Sylvia", "Josephine", "Thelma", "Shannon", "Sheila",
		"Ethel", "Ellen", "Elaine", "Marjorie", "Carrie", "Charlotte", "Monica",
		"Esther", "Pauline", "Emma", "Juanita", "Anita", "Rhonda", "Hazel", "Amber",
		"Eva", "Debbie", "April", "Clara", "Lucille", "Joanne", "Eleanor", "Valerie",
		"Danielle", "Megan", "Alicia", "Suzanne", "Michele", "Gail", "Bertha",
		"Darlene", "Veronica", "Jill", "Erin", "Geraldine", "Lauren", "Cathy",
		"Joann", "Lorraine", "Lynn", "Sally", "Regina", "Erica", "Beatrice",
		"Dolores", "Bernice", "Audrey", "Yvonne", "Annette", "June", "Samantha",
		"Dana", "Stacy", "Ana", "Renee", "Ida", "Vivian", "Roberta", "Holly",
		"Brittany", "Melanie", "Loretta", "Yolanda", "Jeanette", "Laurie", "Katie",
		"Kristen", "Vanessa", "Alma", "Sue", "Elsie", "Beth", "Jeanne", "Vicki",
		"Carla", "Tara", "Rosemary", "Eileen", "Terri", "Gertrude", "Lucy", "Tonya",
		"Ella", "Stacey", "Wilma", "Gina", "Kristin", "Natalie", "Agnes", "Vera",
		"Charlene", "Bessie", "Delores", "Melinda", "Pearl", "Arlene", "Maureen",
		"Colleen", "Allison", "Tamara", "Joy", "Georgia", "Constance", "Lillie",
		"Claudia", "Jackie", "Marcia", "Tanya", "Nellie", "Minnie", "Marlene",
		"Heidi", "Glenda", "Lydia", "Viola", "Courtney", "Marian", "Stella",
		"Caroline", "Dora", "Jo", "Vickie", "Mattie", "Maxine", "Irma", "Mabel",
		"Marsha", "Myrtle", "Lena", "Christy", "Deanna", "Patsy", "Hilda",
		"Gwendolyn", "Jennie", "Nora", "Margie", "Nina", "Cassandra", "Leah",
		"Penny", "Kay", "Priscilla", "Naomi", "Carole", "Brandy", "Olga", "Billie",
		"Dianne", "Tracey", "Leona", "Jenny", "Felicia", "Sonia", "Miriam", "Velma",
		"Becky", "Bobbie", "Violet", "Kristina", "Toni", "Misty", "Mae", "Shelly",
		"Daisy", "Ramona", "Sherri", "Erika", "Katrina", "Claire", "Chuck"
	};
	private static String[] lastNames = {
		"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller",
		"Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White",
		"Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson",
		"Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Hall", "Allen",
		"Young", "Hernandez", "King", "Wright", "Lopez", "Hill", "Scott",
		"Green", "Adams", "Baker", "Gonzalez", "Nelson", "Carter", "Mitchell",
		"Perez", "Roberts", "Turner", "Phillips", "Campbell", "Parker",
		"Evans", "Edwards", "Collins", "Stewart", "Sanchez", "Morris",
		"Rogers", "Reed", "Cook", "Morgan", "Bell", "Murphy", "Bailey",
		"Rivera", "Cooper", "Richardson", "Cox", "Howard", "Ward", "Torres",
		"Peterson", "Gray", "Ramirez", "James", "Watson", "Brooks", "Kelly",
		"Sanders", "Price", "Bennett", "Wood", "Barnes", "Ross", "Henderson",
		"Coleman", "Jenkins", "Perry", "Powell", "Long", "Patterson", "Hughes",
		"Flores", "Washington", "Butler", "Simmons", "Foster", "Gonzales",
		"Bryant", "Alexander", "Russell", "Griffin", "Diaz", "Hayes", "Myers",
		"Ford", "Hamilton", "Graham", "Sullivan", "Wallace", "Woods", "Cole",
		"West", "Jordan", "Owens", "Reynolds", "Fisher", "Ellis", "Harrison",
		"Gibson", "Mcdonald", "Cruz", "Marshall", "Ortiz", "Gomez", "Murray",
		"Freeman", "Wells", "Webb", "Simpson", "Stevens", "Tucker", "Porter",
		"Hunter", "Hicks", "Crawford", "Henry", "Boyd", "Mason", "Morales",
		"Kennedy", "Warren", "Dixon", "Ramos", "Reyes", "Burns", "Gordon",
		"Shaw", "Holmes", "Rice", "Robertson", "Hunt", "Black", "Daniels",
		"Palmer", "Mills", "Nichols", "Grant", "Knight", "Ferguson", "Rose",
		"Stone", "Hawkins", "Dunn", "Perkins", "Hudson", "Spencer", "Gardner",
		"Stephens", "Payne", "Pierce", "Berry", "Matthews", "Arnold", "Wagner",
		"Willis", "Ray", "Watkins", "Olson", "Carroll", "Duncan", "Snyder",
		"Hart", "Cunningham", "Bradley", "Lane", "Andrews", "Ruiz", "Harper",
		"Fox", "Riley", "Armstrong", "Carpenter", "Weaver", "Greene",
		"Lawrence", "Elliott", "Chavez", "Sims", "Austin", "Peters", "Kelley",
		"Franklin", "Lawson", "Fields", "Gutierrez", "Ryan", "Schmidt", "Carr",
		"Vasquez", "Castillo", "Wheeler", "Chapman", "Oliver", "Montgomery",
		"Richards", "Williamson", "Johnston", "Banks", "Meyer", "Bishop",
		"Mccoy", "Howell", "Alvarez", "Morrison", "Hansen", "Fernandez",
		"Garza", "Harvey", "Little", "Burton", "Stanley", "Nguyen", "George",
		"Jacobs", "Reid", "Kim", "Fuller", "Lynch", "Dean", "Gilbert",
		"Garrett", "Romero", "Welch", "Larson", "Frazier", "Burke", "Hanson",
		"Day", "Mendoza", "Moreno", "Bowman", "Medina", "Fowler", "Brewer",
		"Hoffman", "Carlson", "Silva", "Pearson", "Holland", "Douglas",
		"Fleming", "Jensen", "Vargas", "Byrd", "Davidson", "Hopkins", "May",
		"Terry", "Herrera", "Wade", "Soto", "Walters", "Curtis", "Neal",
		"Caldwell", "Lowe", "Jennings", "Barnett", "Graves", "Jimenez",
		"Horton", "Shelton", "Barrett", "Obrien", "Castro", "Sutton",
		"Gregory", "Mckinney", "Lucas", "Miles", "Craig", "Rodriquez",
		"Chambers", "Holt", "Lambert", "Fletcher", "Watts", "Bates", "Hale",
		"Rhodes", "Pena", "Beck", "Newman", "Haynes", "Mcdaniel", "Mendez",
		"Bush", "Vaughn", "Parks", "Dawson", "Santiago", "Norris", "Hardy",
		"Love", "Steele", "Curry", "Powers", "Schultz", "Barker", "Guzman",
		"Page", "Munoz", "Ball", "Keller", "Chandler", "Weber", "Leonard",
		"Walsh", "Lyons", "Ramsey", "Wolfe", "Schneider", "Mullins", "Benson",
		"Sharp", "Bowen", "Daniel", "Barber", "Cummings", "Hines", "Baldwin",
		"Griffith", "Valdez", "Hubbard", "Salazar", "Reeves", "Warner",
		"Stevenson", "Burgess", "Santos", "Tate", "Cross", "Garner", "Mann",
		"Mack", "Moss", "Thornton", "Dennis", "Mcgee", "Farmer", "Delgado",
		"Aguilar", "Vega", "Glover", "Manning", "Cohen", "Harmon", "Rodgers",
		"Robbins", "Newton", "Todd", "Blair", "Higgins", "Ingram", "Reese",
		"Cannon", "Strickland", "Townsend", "Potter", "Goodwin", "Walton",
		"Rowe", "Hampton", "Ortega", "Patton", "Swanson", "Joseph", "Francis",
		"Goodman", "Maldonado", "Yates", "Becker", "Erickson", "Hodges",
		"Rios", "Conner", "Adkins", "Webster", "Norman", "Malone", "Hammond",
		"Flowers", "Cobb", "Moody", "Quinn", "Blake", "Maxwell", "Pope",
		"Floyd", "Osborne", "Paul", "Mccarthy", "Guerrero", "Lindsey",
		"Estrada", "Sandoval", "Gibbs", "Tyler", "Gross", "Fitzgerald",
		"Stokes", "Doyle", "Sherman", "Saunders", "Wise", "Colon", "Gill",
		"Alvarado", "Greer", "Padilla", "Simon", "Waters", "Nunez", "Ballard",
		"Schwartz", "Mcbride", "Houston", "Christensen", "Klein", "Pratt",
		"Briggs", "Parsons", "Mclaughlin", "Zimmerman", "French", "Buchanan",
		"Moran", "Copeland", "Roy", "Pittman", "Brady", "Mccormick",
		"Holloway", "Brock", "Poole", "Frank", "Logan", "Owen", "Bass",
		"Marsh", "Drake", "Wong", "Jefferson", "Park", "Morton", "Abbott",
		"Sparks", "Patrick", "Norton", "Huff", "Clayton", "Massey", "Lloyd",
		"Figueroa", "Carson", "Bowers", "Roberson", "Barton", "Tran", "Lamb",
		"Harrington", "Casey", "Boone", "Cortez", "Clarke", "Mathis",
		"Singleton", "Wilkins", "Cain", "Bryan", "Underwood", "Hogan",
		"Mckenzie", "Collier", "Luna", "Phelps", "Mcguire", "Allison",
		"Bridges", "Wilkerson", "Nash", "Summers", "Atkins", "Wilcox", "Pitts",
		"Conley", "Marquez", "Burnett", "Richard", "Cochran", "Chase",
		"Davenport", "Hood", "Gates", "Clay", "Ayala", "Sawyer", "Roman",
		"Vazquez", "Dickerson", "Hodge", "Acosta", "Flynn", "Espinoza",
		"Nicholson", "Monroe", "Wolf", "Morrow", "Kirk", "Randall", "Anthony",
		"Whitaker", "Oconnor", "Skinner", "Ware", "Molina", "Kirby", "Huffman",
		"Bradford", "Charles", "Gilmore", "Dominguez", "Oneal", "Bruce",
		"Lang", "Combs", "Kramer", "Heath", "Hancock", "Gallagher", "Gaines",
		"Shaffer", "Short", "Wiggins", "Mathews", "Mcclain", "Fischer", "Wall",
		"Small", "Melton", "Hensley", "Bond", "Dyer", "Cameron", "Grimes",
		"Contreras", "Christian", "Wyatt", "Baxter", "Snow", "Mosley",
		"Shepherd", "Larsen", "Hoover", "Beasley", "Glenn", "Petersen",
		"Whitehead", "Meyers", "Keith", "Garrison", "Vincent", "Shields",
		"Horn", "Savage", "Olsen", "Schroeder", "Hartman", "Woodard",
		"Mueller", "Kemp", "Deleon", "Booth", "Patel", "Calhoun", "Wiley",
		"Eaton", "Cline", "Navarro", "Harrell", "Lester", "Humphrey", "Norris",
		"Parrish"
	};

	private static int foreNameIndex = 0;
	private static int lastNameIndex = 0;
	private static boolean hasRandomised = false;
	private static Random rand = null;
	
	public synchronized static void setRandomiser(Random r)
	{
		rand = r;
	}
	
	/**
	 * Generates a (possibly non-unique) player name
	 * @return
	 */
	public synchronized static String getName()
	{
		if (!hasRandomised)
		{
			SortUtil.sort(foreNames);
			SortUtil.sort(lastNames);
			hasRandomised = true;
		}
		String fName = foreNames[foreNameIndex];
		String lName = lastNames[lastNameIndex];

		if (rand.nextBoolean())
		{
			foreNameIndex++;
			if (foreNameIndex == foreNames.length) foreNameIndex = 0;
		}
		else
		{
			lastNameIndex++;
			if (lastNameIndex == lastNames.length) lastNameIndex = 0;			
		}

		return fName + ' ' + lName;
	}

	private NameGenerator()
	{
		// Nothing to see here. Move along, citizen
	}

}
