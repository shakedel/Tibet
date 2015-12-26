import general.Interval;
import general.IndexPair;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import algorithms.APBT;




public class main {

	/**
	 * @param args
	 */
	
	private static String readFile(String path) throws IOException {
		  FileInputStream stream = new FileInputStream(new File(path));
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    /* Instead of using default, pass in a decoder. */
		    return Charset.defaultCharset().decode(bb).toString();
		    //return Charset.defaultCharset().toString();
		  }
		  finally {
		    stream.close();
		  }
	}
	
	public static void main(String[] args) {
		int maxThreadPoolSize = 5;
		int stepSize = 12500;
		int readSize = 25000;
		int minLength = 60;
		int maxErrorDistance = 10;
		ConcurrentCounter TCounter = new ConcurrentCounter();
		String str1 = "";
		String str2 = "";
		resultsContainer rContainer = new resultsContainer(0.8);
		
		try
		{
			str1 = readFile("F:\\lior_text_project_2\\Data\\bigCandidate\\out.txt");
			str2 = readFile("F:\\lior_text_project_2\\Data\\bigCandidate\\out2.txt");
		}
		catch ( Exception e ) {
		    throw new RuntimeException("msg",e);
		}

			
		ExecutorService executor = Executors.newFixedThreadPool(maxThreadPoolSize);

		for (int i=0; i < str1.length(); i=i+stepSize)
		{
			for (int j=0; j < str2.length(); j=j+stepSize)
			{
				IndexPair firstIndexPair = new IndexPair(i, j);
				IndexPair secondIndexPair = new IndexPair(Math.min(i + readSize, (str1.length()-1)), Math.min(j + readSize, (str2.length()-1)));
				
				Interval workingInterval = new Interval(firstIndexPair, secondIndexPair);
				int threadId = TCounter.increment();
				
				Runnable worker = new APBTWorkierThread(str1, str2, workingInterval, minLength, maxErrorDistance, rContainer, TCounter, threadId);
				executor.execute(worker);
						
			}
		}
		
		
        executor.shutdown();
        System.out.println("waiting..");
        while (!executor.isTerminated())
        {
        	try
        	{
        		System.out.println("Writing results...");
        		int sleepMinutes = 1;
        		String outStr = "F:\\lior_text_project_2\\javaResult\\" + System.currentTimeMillis() + "_out.txt";
        		rContainer.writeResults(outStr);
        		Thread.sleep(sleepMinutes*60*1000);
        	}
        	catch(InterruptedException e)
        	{
        		
        	}
        	
        }
        
		String outStr = "F:\\lior_text_project_2\\javaResult\\" + System.currentTimeMillis() + "_out.txt";
		rContainer.writeResults(outStr);
		System.out.println("finished");
        
		// TODO Auto-generated method stub
//		System.out.println("Hello World!");
//		int maxDiff = 3;
//		int minLength = 8;
//		String seq1 = "hello how are you?";
//		String seq2 = "hello goware you?";
//		
//		
//		
//		long start=System.currentTimeMillis();
//		
//		str1 = " @148b #, ,rgya gar skad du, s'u tra sa mutstsh'a ya, bod skad du, mdo kun las btus pa, bam po dang po, sangs rgyas dang byang chub sems dpa' thams cad la phyag 'tshal lo, ,sangs rgyas 'byung ba shin tu rnyed par dka' ste, sangs rgyas 'byung ba de shin tu rnyed par dka' bar ci mngon zhe na, mdo sde du ma tshad mar gyur pa las shes te, dam pa'i chos padma dkar po las 'byung ba, dge slong dag de bzhin gshegs pa dgra bcom pa yang dag par rdzogs pa'i sangs rgyas rnams ni 'byung ba rnyed par dka' ste, bskal pa bye ba khrag khrig brgya stong phrag mang por yang 'jig rten du de bzhin gshegs pa mthong yang srid mi mthong yang srid de, dper na au dumw'a ra'i me tog bzhin no zhes gsungs so, ,rnam par gtan la dbab pa'i rgyal po'i mdo las 'byung ba, kun dga' bo 'di lta ste, au dumw'a ra'i me tog ni sangs rgyas skye ba dang dus gcig tu 'byung ste, kha dog dang ldan zhing gsal te, 'od gsal la dri ni dpag tshad gcig khor yug tu khyab par byed do, ,me tog des na rab rib med par byed, dran pa yongs su sbyong bar byed, nad kyang rab tu zhi bar byed, mun pa yang sel cing snang bar yang byed, dri zhim po yang 'byin pas khams bzhi dang bar 'gyur te, 'khor los sgyur ba'i rgyal po thams cad la yang me tog de mi 'byung na, sems can tshul khrims 'chal pa rnams la sangs rgyas 'byung ba dang mtshungs pa'i me tog lta 'byung bar ga la 'gyur zhes gsungs so, ,me tog de ci 'dra, gang du nam skye zhe na, rtogs pa brjod pa'i mdo las 'byung ba, mtsho chen po ma dros pa 'das pa'i byang phyogs na ri rtse lnga pa zhes bya ba de'i pha rol na au dumw'a ra'i tshal yod de, nam sangs rgyas bcom ldan 'das dag dga' ldan gyi lha'i ris nas babs te, yum gyi lhums su zhugs pa de'i tshe tshal de kha 'bu bar 'gyur ro, ,nam yum yi lhums nas bltams pa de'i tshe tshal de kha 'bye [?] bar 'gyur ro, ,nam bla na med pa yang dag par rdzogs pa'i byang chub mngon par rdzogs par sangs rgyas pa de'i tshe tshal de'i me tog kyang rab tu rgyas par 'gyur ro, ,nam tshe'i 'du byed @149a *, ,bor ba de'i tshe tshal de rnyings par 'gyur ro, ,nam yongs su mya ngan las 'das pa de'i tshe tshal de'i lo ma dang, me tog dang, 'dab ma dang, 'bras bu yang lhags par 'gyur ro, ,au dumw'a ra'i me tog de'i tshad la ni shing rta'i 'phang lo tsam mo, ,nam sangs rgyas bcom ldan 'das dag rgyal rigs kyi nang du 'byung ba de'i tshe, me tog de kha dog dkar rab tu 'gyur ro, ,nam bram ze'i rigs kyi nang du 'byung ba de'i tshe me tog de dkar por 'gyur ro zhes gsungs so, ,byang chub sems dpa'i sde snod las kyang, kye rgyal ba'i sras de bzhin gshegs pa ni rnyed par dka' ste, mchog tu rnyed par dka'o zhes gsungs so, ,bcom ldan 'das kyis ye shes shin tu rgyas pa'i mdo las kyang, byang chub sems dpa' rgyal po'i drang srong snyan pa chen pos bstan pa, grogs po dag de bzhin gshegs pa gang byed pa dang ldan pa'i chos ston pa dag ni brgya la brgya lam na 'jig rten du 'byung ngo zhe'o, ,zla ba'i snying po'i le'u las kyang bstan te, sangs rgyas rnams 'jig rten du 'byung ba ni rnyed par dka' ste, au dumw'a ra'i me tog dper brjod pa dang mtshungs so zhes 'byung ngo, ,'phags pa sdong po bkod pa'i mdo las kyang, gang dag mthong zhing thos pa dang, bsnyen bkur byas pa don yod pa, ,'jig rten 'dren pa 'jig rten na, ,bskal pa bye bar rnyed par dka', ,zhes gsungs so, ,bskal pa bzang po'i mdo las kyang, bskal pa bzang po 'di'i 'og tu sangs rgyas med pa'i bskal pa drug cu 'byung ngo; ,de'i 'og tu bskal pa snyan pa chen po zhes bya ba 'byung ste, bskal pa gcig po de las sangs rgyas khri 'byung ngo, ,bskal pa snyan pa chen po de'i 'og tu sangs rgyas med pa'i bskal pa brgyad khri 'byung ngo, ,de'i 'og tu bskal pa skar ma lta bu zhes bya ba 'byung ste, bskal pa de gcig la sangs rgyas brgyad khri 'byung ngo, ,bskal pa skar ma lta bu de'i 'og tu bskal pa sum brgyar sangs rgyas mi 'byung ngo, ,de'i 'og tu yon tan bkod pa zhes bya ba'i bskal pa 'byung ste, bskal pa de gcig la sangs rgyas brgyad khri bzhi stong 'byung ngo zhes gsungs so, , @149b mir 'gyur ba ni shin tu rnyed par dka' ste, ji ltar zhe na, 'di lta ste, yang dag par ldan pa'i lung las 'byung ba, dge slong dag 'di lta ste, dper na, sa chen po 'di chu 'ba' zhig tu gyur la, de'i nang du mi zhig gis shing bu ga gcig pa zhig bor te, de shar gyi rlung gis ni nub phyogs su phul, nub kyi rlung gis ni shar phyogs su phul, byang gi rlung gis ni lho phyogs su phul, lho'i rlung gis ni byang phyogs su phul la, de na rus sbal zhar ba tshe lo brgya phrag du ma thub pa, lo brgya phrag du mar 'tsho ba lo brgyar 'das shing lan cig steng du 'byung ba de bu ga gcig pa'i shing der mgrin pa 'jug na dge slong dag 'di ji snyam du sems, rus sbal zhar ba tshe lo brgya phrag du ma thub pa, lo brgya phrag du mar 'tsho ba de lo brgya 'das shing lan cig steng du 'byung ba des, shing bu ga gcig pa'i shing der mgrin pa chud dam, bcom ldan 'das la dge sbyong dag gis gsol pa, bcom ldan 'das stes na 'gyur lags so, ,bde bar gshegs pa stes na 'gyur lags te, rus sbal zhar ba tshe lo brgya phrag du ma thub pa, tshe lo brgya phrag du mar 'tsho ba, de lo brgya 'das shing lan cig steng du 'byung ba des, shing bu ga gcig pa der mgrin pa chud kyang gda', mi chud kyang gda'o, ,bcom ldan 'das kyis bka' stsal pa, dge slong dag de bzhin du de bzhin gshegs pa rnams brgya la brgya lam na 'jig rten du 'byung zhing zhi bar 'gyur ba, yongs su mya ngan las 'das par 'gyur ba, rdzogs pa'i byang chub tu 'gro bar byed pa, bde bar gshegs pas rab tu bstan pa'i chos ston to, ,mir gyur pa yang brgya la brgya lam na stes na 'thob ste, shin tu rnyed par dka'o zhes gsungs so, ,dal ba 'byor pa rnyed par dka' ste, dal ba 'byor pa rnyed par dka' bar ci mngon zhe na, mdo sde'i bye brag mang po brjod pa las mthong bas mngon te, gcig la 'phros pa'i lung las 'byung ba, dge slong dag mi khom pa brgyad po 'di dag ni, gang zag tshangs par spyod pa la gnas pa'i dus ma yin te, brgyad gang zhe na, dge slong dag 'di la de bzhin gshegs pa 'jig rten du 'byung ste zhi bar 'gyur @150a *, ,ba; yongs su mya ngan las 'das par 'gyur ba, rdzogs pa'i byang chub tu 'gro bar byed pa, bde bar gshegs pas rab tu bstan pa'i chos kyang ston na, gang zag de sems can dmyal bar skyes pa yin te, da ni gang zag tshangs par spyod pa gnas pa'i dus ma yin pa mi khom pa dang po'o, ,de bzhin du dud 'gro'i skye gnas dang, gshin rje'i 'jig rten dang, lha tshe ring po dang, mtha' 'khob kyi mi dang, rku 'phrog byed pa dang, kla klo dang, brnab sems can dang, gnod sems can gang du dge slong dang, dge slong ma dang dge bsnyen dang, dge bsnyen ma mi 'ong ba'i nang du skyes pa yin no, ,'jig rten 'dir de bzhin gshegs pa 'byung ste chos kyang ston la, gang zag de dbus kyi mi'i nang du yang skyes na de glen zhing lug ltar lkugs pa la lag brda byed cing, legs par smras pa dang, nyes par smras pa'i don go ba'i mthu med pa yin no, ,de bzhin gshegs pa 'jig rten 'dir byung ste, chos kyang ston la, gang zag de dbus kyi mi'i nang du yang skyes shing, de glen pa ma yin la, ,lug ltar lkugs pa ma yin pa nas, don go ba'i mthu yod pa'i bar du yang 'gyur na, de log par lta zhing phyin ci log tu mthong ste, de 'di ltar byin pa yang med, mchod sbyin yang med, sbyin sreg byas pa yang med, legs par byas pa dang, nyes par byas pa'i las kyi 'bras bu rnam par smin pa yang med, 'jig rten 'di yang med, 'jig rten pha rol yang med, pha yang med, ma yang med de, 'jig rten na dge sbyong dang, bram ze yang dag par song ba dang, yang dag par zhugs pa dang, dgra bcom pa 'jig rten rig pa gang rang gi mngon par shes pas 'jig rten 'di dang 'jig rten pha rol mngon sum du bya ste, khong du chud par byed pa yang med par lta ba yin no, ,gzhan yang gang zag de dbus kyi mi rnams kyi nang du skye ste, glen pa ma yin pa nas legs par smras pa dang, nyes par smras pa rnams kyi don go ba'i mthu yod pa'i ba'i gtam brjod de, thos nas kyang the tshom mi za zhing chos thams cad la chags pa med par 'jug ,phung po dang, khams dang, skye mched la yang mi chags, chos thams cad rang bzhin gyis stong par yang @152b yid ches te, sangs rgyas kyi ye shes tshol zhing bag yod pa la gzhol ba yin no, ,bag yod pa gang zhe na, gang dbang po sdom pa dang, rang gi sems 'dul ba dang, gzhan gyi sems bsrung ba'o zhes gsungs so, ,zla ba'i snying po'i le'u las, dkon mchog gsum la sems can rnams dad cing dang ba ni dkon te, dper na yid bzhin gyi nor bu rin po che dper brjod pa bzhin no zhes gsungs so, ,klu'i rgyal po rgya mtshos zhus pa las kyang, klu'i bdag po byang chub sems dpa' chos lnga dang ldan pa dag ni dad pa can yin te, lnga gang zhe na, mos pa'i stobs dang, las kyi rnam par smin pa la 'jug pa'i stobs dang, byang chub kyi sems mi 'dor ba'i stobs dang, yi dam la brtan pa'i stobs dang, mi dge ba'i chos thams cad spangs te, nyes par byas pa thams cad bzod pa'i stobs dang ldan pa'o zhes gsungs so, ,de bzhin gshegs pa'i yon tan dang ye shes bsam gyis mi khyab pa'i yul la 'jug pa bstan pa'i mdo las kyang, 'phags pa byang chub sems dpa' sems dpa' chen po sgrib pa thams cad rnam par sel bas smras pa, 'jam dpal chos lnga po 'di dag la mos na byang chub sems dpa' sems dpa' chen po yon tan gyi bye brag 'di dag dang gzhan grangs med pa yang thob par 'gyur ro, ,lnga gang zhe na, chos thams cad gnyen po med pa dang, ma skyes pa dang, ma 'gags pa dang, brjod du med par mos pa dang, lhun gyis grub cing rnam par mi rtog pa de bzhin gshegs pa'i spyod pa dang, spyod lam dang, bya ba dang, 'jug pa dang, gnas 'dzam bu'i gling  ";
//		str2 = "rgya gar skad du shiksha sa mutstsha ya: bod skad du bslab pa kun las btus pa bam po dang po sangs rgyas dang byang chub sem dpa' thams cad la phyag 'tshal lo; gang ma thos pas dmyal ba la sogs g-yang sar ni bsreg la sogs pa'i sdug bsngal mi bzad mtha' yas dag sems ma zhi ba khyed kyis yang yang myong gyur pa de ni mnyan phyir gus pa shin tu rgya cher bsten gang thos nas kyang g-yeng ba med pa'i bdag can gyis; sdig spong sngon byas mang po ma lus zang byed la sngon chad ma thob pa yi bde ba'ang rab 'thob cing bde ba las ni nam yang nyams par mi 'gyur dang rdzogs pa'i byang chub sems dpa'i bde mchog mi zad dang sangs rgyas go 'phang mnyam med phun tshogs thob byed pa shin tu rnyed dka'i rin chen chos de deng thob na legs par brjod pa 'di la dal thob gus par nyon khams gsum phan mdzad gcig po de gsung mnyan bya'i phyir lha dang klu dang grub pa dri za gnod sbyin dang mkha' lding lha min mi 'am ci dbang 'dre la sogs  dgar bcas nyan par mos shing yid dang 'dir shog shig bder gshegs chos kyi sku mnga' sras bcas dang phyag 'os kun la'ang gus par phyag 'tshal te bde gshegs sras kyi sdom la 'jug pa rnams mdor bsdus don gyi ngag gis brjod par bya sngon chad ma byung ci yang 'dir brjod med sdeb sbyor mkhas pa'ang bdag la yod min te de phyir gzhan gyi don du bdag mi 'bad rang gi yid la bsgom phyir ngas 'di brtsams dge ba bsgom phyir bdag gi dad pa'i shugs 'di dag gis kyang re zhig 'phel @3b 'gyur la bdag dang skal ba mnyam pa gzhan gyis kyang ji ste 'di dag mthong na don yod 'gyur dal 'byor shin tu rnyed bar dka' ba dang skyes bu'i don bsgrub 'di ni rnyed gyur pas gal te 'di la phan pa ma bsams na phyis 'di yang dag 'byor par ga la 'gyur; ji skad du 'phags pa sdong po bkod pa'i mdor 'phags pa rgyal ba'i drod kyi skye mched kyi rnam par thar pa las mi khom pa brgyad las ldog pa yang rnyed par dka' mir 'gyur ba yang rnyed par dka' dal ba phun sum tshogs pa rnam par dag pa yang rnyed par dka' sangs rgyas 'byung ba yang rnyed par dka' dbang po mtshang ba med pa yang rnyed par dka' sangs rgyas kyi chos mnyan pa yang rnyed par dka' skyes bu dam pa dang 'grogs pa yang rnyed par dka' yang dag pa'i dge ba'i bshes gnyen rnams kyang rnyed par dka' yang dag pa'i tshul rjes su bstan pa nye bar sgrub pa yang rnyed par dka' yang dag par 'tsho ba yang rnyed par dka'  mi'i 'jig rten na chos dang rjes su mthun pa'i chos la nan tan byed pa yang rnyed par dka' na zhes gsungs pa bzhin no de'i phyir rnam pa de lta bu dang phrad pa la brten nas sems can chen po gang la gang tsho bdag dang gzhan dag kyang 'jigs dang sdug bsngal mi 'dod pa de tshe de srung gzhan mi gang bdag dang bye brag ci zhig yod snyam pa de lta bu'i so sor spyod pa skyes nas des bdag dang sems can gyi khams rnams kyi sdug bsngal mthar 'byin 'dod pa dang bde ba'i mthar yang 'gro 'dod pas dad pa'i rtsa ba brtan byas te byang chub la yang blo brtan bya 'di ltar dkon mchog ta la la'i gzungs las rgyal dang rgyal ba'i chos la dad gyur cing sangs rgyas sras kyi spyod la dad byed pas byang chub bla na med la dad gyur nas skyes bu chen po rnams kyi sems skye'o dad pa sngon 'gro ma ltar ba skyed pa ste yon tan thams cad srung zhing 'phel bar byed dogs pa sel zhing chu bo rnams las @4a * sgrol; dad pa bde legs grong khyer mtshon byed cing dad pa rnyog pa med cing sems dang byed nga rgyal spong zhing gus pa'i rtsa ba yin dad pa gter dang nor dang rkang pa'i mchog lag pa bzhin du dge sdud rtsa ba yin dad pa yongs su gtong la dga' bar byed; dad pa rgyal ba'i chos la rab dga' skyed dad pa yon tan ye shes khyad par byed sangs rgyas go 'phang ston cing thob byed yin dbang po rno zhing rab tu gsal bar byed dad pa'i stobs la gzhan gyis mi brdzi zhing nyon mongs rnam par sel ba'i gzhi rten yin dad pa rang byung yon tan rnams kyang tshol dad pa chags pa'i sgo la chags pa med mi dal spong zhing dal ba mchog cig yin dad pa bdud kyi lam las 'da' byed cing thar pa'i lam mchog ston par byed pa yin sa bon ma rul yon tan rnams kyi rgyu; dad pa byang chub shing ni rnam par skyed ye shes khyad par yon tan 'phel bar byed dad pa rgyal ba thams cad ston byed yin gang dag rtag tu sangs rgyas dad gus bcas de dag tshul khrims bslab pa yongs mi 'dor gang dag tshul khrims bslab pa mi 'dor ba yon tan ldan de yon tan ldan bas bstod gang dag rtag tu chos la dad gus bcas de dag rgyal ba'i chos nyan ngoms pa med gang dag rgyal ba'i chos nyan mi ngoms pa de dag chos la mos pa bsam mi khyab gang dag rtag tu dge 'dun dad gus bcas |  de dag mi ldog dge 'dun dad pa yin gang dag mi ldog dge 'dun rab dad pa de dag dad pa'i stobs kyis mi ldog 'gyur gang dag dad pa'i stobs kyis mi ldog pa de dag dbang po rno zhing rab tu gsal gang dag dbang po rno zhing rab gsal ba de dag sdig pa'i grogs po rnam par spangs gang dag sdig pa'i grogs po rnam spangs pa de dag chos ldan grogs kyis yongs su bzung gang dag chos ldan grogs kyis yongs bzung ba de dag dge ba rgya chen sogs par byed gang dag dge ba rgya chen sogs byed pa bdag @4b nyid chen po de dag rgyu stobs ldan bdag nyid chen po rgyu stobs gang ldan pa de dag mos pa'i khyad par rgya chen 'gyur gang dag mos pa'i khyad par rgya che ba de dag rgyal ba kun gyis rtag byin brlabs gang dag rgyal ba kun gyis byin brlabs pa de dag byang chub don du sems kyang skye gang dag byang chub don du sems skye ba de dag drang srong chen po'i yon tan brtson drang srong chen po'i yon tan gang brtson pa de dag rigs mtho sangs rgyas rigs su skye gang dag rigs mtho sangs rgyas rigs skyes pa de dag mnyam zhing sbyor dang mi sbyor spangs mnyam zhing sbyor dang mi sbyor gang spangs pa de dag dad dang bsam pa rnam par dag gang dag dad dang bsam pa rnam dag pa de dag lhag pa'i bsam pa dam pa mchog gang dag lhag bsam dam pa mchog ldan pa de dag rtag tu pha rol phyin pa spyod; gang dag rtag tu pha rol phyin spyod pa de dag theg pa chen po 'di la zhugs gang dag theg pa chen po 'dir zhugs pa de dag sangs rgyas rnams la sgrub pas mchod gang dag sgrub pas sangs rgyas mchod byed pa de dag sangs rgyas rjes dran mi phyed 'gyur; gang dag sangs rgyas rjes dran mi phyed pa de dag rtag tu sangs rgyas bsam yas mthong gang dag sangs rgyas bsam yas rtag mthong ba de la nam yang sangs rgyas mi bzhugs min gang la nam yang sangs rgyas mi bzhugs min de dag bar skabs chos las nyams mi 'gyur zhes bya ba la sogs pa gsungs pa dang  dad pa'i rtsa ba can gyi yon tan rgya chen po mtha' yas pa zhig der gsungs te de rdzogs nas yang mdor bsdus te gsungs pa 'di 'dra chos ni gang dag dad gyur pa sems can so so skye bo'i tshogs na dkon gang dag dge bsags bsod nams byas gyur pa de dag rgyu yi stobs kyis 'di la dang gang g'a zhing bcu'i rdul snyed sems can la bde ba kun gyis bskal par bsnyen bkur yang chos 'dir dad pa'i bsod nams ci 'dra ba bsod nams khyad @5a * par de 'dra de la med ces gsungs pa yin no de skad du chos bcu pa'i mdo las kyang gang gis 'dren pa nges 'byung ba dad pa theg pa mchog yin te de'i phyir blo dang ldan pa'i mis dad pa'i rjes su 'brang ba bsten ma dad pa yi mi dag la  dkar po'i chos rnams mi skye ste sa bon me yis tshig pa las myu gu sngon po ji bzhin no zhes gsungs so de bas ni 'phags pa rgya cher rol pa'i mdo las kyang kun dga' bo dad pa la sbyor bar bya ste 'di ni de bzhin gshegs pa la gsol ba 'debs pa'o zhes bstan to de skad du seng ges zhus pa'i mdo las kyang dad pas mi khom spang bar 'gyur zhes bka' stsal pa yin no de ltar dad pa'i rtsa ba brtan par byas la bsod nams thams cad sdud pa'i phyir na byang chub kyi sems brtan par bya'o ji skad du seng ges zhus pa las bcom ldan 'das la rgyal bu seng ges zhus pa  chos rnams thams cad bsdu ba ni chos gang gis bcom ldan 'das kyis bka' stsal pa sems can thams cad thar bya'i phyir byang chub don du sems btud pa des ni chos rnams bsdu ba 'thob des ni dga' ba thob par 'gyur zhes gsungs pa bzhin no de skad du 'phags pa sdong po bkod pa'i mdo las kyang rigs kyi bu byang chub kyi sems ni sangs rgyas kyi chos thams cad kyi sa bon lta bu'o 'gro ba thams cad kyi chos dkar po rnam par 'phel bar byed pas zhing lta bu'o 'jig rten thams cad rten pas sa lta bu'o zhes bya ba nas byang chub sems dpa' thams cad yongs su srung bas pha lta bu'o zhes bya ba'i bar dang  de bzhin du sbyar te dbul ba thams cad yang dag par gcod pas rnam thos kyi bu lta bu'o don thams cad yang dag par sgrub pas yid bzhin gyi nor bu'i rgyal po lta bu'o bsam pa thams cad yongs su rdzogs par byed pas bum pa bzang po lta bu'o nyon mongs  @5b pa'i dgra pham par bya ba la mdung thung lta bu'o tshul bzhin ma yin pa yid la byed pa 'gebs pas go cha lta bu'o nyon mongs pa'i mgo lhung bar byed pas ral gri lta bu'o nyon mongs pa'i shing gcod pas sta re lta bu'o 'tshe ba thams cad las skyob pas mtshon cha lta bu'o; 'khor ba'i chu'i nang na gnas pa steng du 'byin pas mchil pa lta bu'o sgrib pa dang khebs pa'i rtsa ba thams cad rnam par 'thor bas rlung gi dkyil 'khor lta bu'o byang chub sems dpa'i spyod pa dang smon lam thams cad bsdus pa'i phyir mdor bstan pa lta bu'o |  lha dang mi dang lha ma yin du bcas pa'i 'jig rten la mchod rten lta bu ste rigs kyi bu de ltar byang chub kyi sems ni yon tan de dag dang yon tan gyi bye brag tshad med pa gzhan dang yang ldan no zhes brjod do so so'i skye bo la yang byang chub kyi sems skye ste 'di tshig tsam ni ma yin no zhes bya bar ji ltar shes she na mdo sde du ma mthong ba las te re zhig 'phags pa dri ma med par grags pas bstan pa las  'jig tshogs la "; 
//		algorithms.APBT newAPBT = new algorithms.APBT(str1.toCharArray(), str2.toCharArray(), minLength, maxErrorDistance);
//		newAPBT.process();
		//newAPBT.processByColumn(str2.toCharArray(), str1.toCharArray());
//			
//				
//		long howlong=System.currentTimeMillis()-start;
//		System.out.println("Processed in "+howlong+" ms.");
//		List solutions=newAPBT.getSolutions();
//		System.out.println("Produced output size="+solutions.size());
//		List maximalsolutions=newAPBT.getMaximalSolutions();	
//		int a =3;
//		a++;
//		System.out.println("Maximal output size="+maximalsolutions.size());
//		
//		for(int i=0;i<maximalsolutions.size();i++)
//		{
//			Interval curr=(Interval)maximalsolutions.get(i);
//			System.out.println(curr);
//			System.out.println(seq1.substring(curr.getStart().getIndex1(),curr.getEnd().getIndex1()+1));
//			System.out.println(seq2.substring(curr.getStart().getIndex2(),curr.getEnd().getIndex2()+1));
//		}
		
//		String PathA = "C:\\mhumanseminar\\textA\\";
//		String PathB = "C:\\mhumanseminar\\textB\\";
//		String PathResults = "C:\\mhumanseminar\\results\\";
//	
//		int minLength = 60;
//		int maxErrorDistance = 10;
//		
//		File fA = new File(PathA);
//		ArrayList<File> filesA = new ArrayList<File>(Arrays.asList(fA.listFiles()));
//		
//		File fB = new File(PathB);
//		ArrayList<File> filesB = new ArrayList<File>(Arrays.asList(fB.listFiles()));
//		
//		int numOfFilesA = filesA.size();
//		int numOfFilesB = filesB.size();
//		
//		for (Integer i = 0; i < numOfFilesA; i++)
//		{
//			System.out.println("Dealing with file number " + i.toString() + " from A and all the rest from B");
//			String strA;
//			
//			try
//			{
//				strA = readFile(filesA.get(i).getPath());
//			}
//			catch ( Exception e ) {
//			    throw new RuntimeException("msg",e);
//			}
//			
//			for (Integer j = 0; j < numOfFilesB; j++)
//			{
//				String strB;
//				try
//				{
//					strB = readFile(filesB.get(j).getPath());
//				}
//				catch ( Exception e ) {
//				    throw new RuntimeException("msg",e);
//				}
//				
//				algorithms.APBT newAPBT = new algorithms.APBT(strA.toCharArray(), strB.toCharArray(), minLength, maxErrorDistance);
//				newAPBT.process();
//				newAPBT.processByColumn(strB.toCharArray(), strA.toCharArray());
//				
//				List maximalsolutions = newAPBT.getMaximalSolutions();
//				
//				if (maximalsolutions.size() > 0)
//				{
//					System.out.println("Found " + ((Integer)maximalsolutions.size()).toString() + " max solution between " + i.toString() + " from A and " + j.toString() + " from B");
//					
//					for(Integer ms=0;ms<maximalsolutions.size();ms++)
//					{
//							Interval curr=(Interval)maximalsolutions.get(ms);
//							
//							try
//							{
//								  // Create file 
//								FileWriter fstream = new FileWriter(PathResults + i.toString() + "_" + j.toString() + "_" + ms.toString() + ".txt");
//								BufferedWriter out = new BufferedWriter(fstream);
//								//out.write("Hello Java");
//								//Close the output stream
//								
//								out.write(curr.toString());
//								out.newLine();
//								out.write(strA.substring(curr.getStart().getIndex1(),curr.getEnd().getIndex1()+1) + "\n");
//								out.newLine();
//								out.write(strB.substring(curr.getStart().getIndex2(),curr.getEnd().getIndex2()+1) + "\n");
//								out.newLine();
//								out.close();
//							}
//							catch (Exception e)
//							{//Catch exception if any
//								System.err.println("Error: " + e.getMessage());
//							}
//						  
//							
//							
//					}
//				}
//
//				
//			}
//		}
//		
	}

}






