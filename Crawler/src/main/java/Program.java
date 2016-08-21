import crawler.GuiaDelOcioCrawler;
import db.DbClient;

public class Program {

	public static void main(String[] args) throws Exception{
		
		if (args.length < 1)
		{
			System.out.println("You should invoke program passing as argument mongo ip");
			return;
		}

		DbClient mongoDbClient = new DbClient (args[0],"taxiwisedb");
		GuiaDelOcioCrawler ocioCrawler = new GuiaDelOcioCrawler(mongoDbClient);

		ocioCrawler.doCrawl(GuiaDelOcioCrawler.THEATERHOMEPAGE);
		System.out.println("Crawling finalizado Theater: " + ocioCrawler.getNumObrasTeatro());
		System.out.println("Unique events Theater: " + ocioCrawler.getTheaterPlays().keySet().size());
		
		ocioCrawler.doCrawl(GuiaDelOcioCrawler.CONCERTHOMEPAGE);
		System.out.println("Crawling finalizado Concerts: " + ocioCrawler.getNumConcerts());
		System.out.println("Unique events Concerts: " + ocioCrawler.getConcerts().keySet().size());
		
		ocioCrawler.finalize();

	}
}
