Copyright 2023-2024 Tunsoiu Dan-Andrei 325CA UNSTPB ACS

Problema VMCHECKER: desi pun fisierul de .git nu primesc punctajul pe git.

Avem urmatoarele clase singleton:
-Library care va fi libraria de cantece a alicatiei, si care contine si o functie
Top5Songs care foloseste un bubblesort pentru a afla primele 5 cantece dupa
numarul de likeuri.
-Userbase, care va contine o lista cu toti userii aplicatiei, o metoda de add
User, o metoda de afisare a Userilor online si o metoda de afisare a tuturor
userilor si o metoda de delete User
-Podcast library, care va tine o lista cu toate Podcasturile din aplicatie, 
o metoda de add Podcast si o metoda de delete Podcast
-Playlist library, care va retine o lista cu toate playlisturile create, si o 
functie de Top5Playlist care functioneaza asemanator cu ceea de Top5Songs din 
clasa Library
-Album libraray, care va contine o lista cu toate albumele din program, si o
metoda de Top5Albums, o metoda de add Album si o metoda de Remove Album

Avem si urmatoarele principii de POO:
-MOSTENIREA
-OVERWRITING
-OVERLOADING

Design Patternuri utilizate:
-Singletone este utilizat la clasele de tipul librarie (Library, UserLibrary,
AlbumLibrary, etc.) deoarece ne dorim sa avem o librarie pentru intreaga apli-
catie si aceasta sa fie unica, ceea ce poate fi facut perfect cu ajutorul
design patternului singletone

-Visitor este utilizat in cadrul clasei User pentru comanda de currentPage care
afiseaza pagina pe care se afla userul cu ajutorul interfetelor Visitor si Visi-
table. Userul implementeaza Visitable deoarece ne dorim sa putem accessa paginile
diferitilor useri, iar Visitor este implementat de o clasa speciala numita
PrintCurrentPage care atunci cand user vrea sa vada pagina pe care se afla el
o sa utilizeze aceasta clasa pentru a "visita" userul a carui pagina se afla el
si o sa o afiseze. Acesta aspect de a visita alti useri si de a le afisa paginile
este motivul pentru care am ales design paternul Visitor

-Observer este implementat cu ajutorul interfetei NotifObserver care contine
o clasa de tipul notification si metoda addNotification. Aceasta interfata este
implementata de catre user caruia o sa putem sa ii adaugam notificari. A doua
parte a acestui design patern se afla in clasele Artist si Host care contin o
lista de Useri numita subscribers. Ideea de baza este ca atunci cand un Artist
sau un Host fac o actiune de care ar trebui sa fie notificati useri atunci o sa
apelam pentru fiecare user din lista subscribers o sa apelam metoda addNotifica-
tion ca sa le adaugam notificarea necesara. Aceasta legatura de un Artist la 
n Useri sau de un Host la n Useri si conceptul ca Useri trebuie sa stie actiunile
pe care le face Artistul/Hostul ma facut sa aleg design patternul Observer

-Command este ultimul design pattern utilizat in tema si este folosit in User
pentru a tine minte paginile pe care sa aflat acesta. Ideea de baza este ca avem
o interfata numita CommandPage cu metodele execute si undo care va fi implementata
de clasa Page care este practic comanda de schimbare a paginilor, acesta se afla
in interiorul clasei PageHistory care este istoria paginilor pe care sa aflat
userul si care contine doua liste undoLs si redoLs, prima tine minte comenzile
pe care le-a facut userul, si adoua tine minte comenzile la care a dat undo userul.
Astfel cand userul o sa schimbe pagina o sa fie instantiat un obiect de tipul
Page caruia o sa ii dam datele pagini pe care ne aflam si pagini pe care vrem
sa ne aflam si pe care o sa il punem in lista undoLs la final si o sa ii executam
metoda execute. Cand vrem sa mergem la pagina anterioara dam undo la ultimul element
din undoLs, il scoatem din undoLS si il punem in redoLs, principiul este acelasi
si pentru nextPage. Am ales sa implementez design patternul command deoarece
era necesar sa tinem minte istoria comenzilor de changePage ceea ce este exact
scopul pentru care a fost creat patternul Command

In main o sa adaugam mai intai datele necesare la clasele precizate anterior, si
o sa resetam lista de Playlisturi, dupa care o sa facem citirea comenzilor cu 
ajutorul unei clase Command care are toate fildurile posibile pentru o comanda, 
dupa care o sa parcurgem lista de commenzi si in functie de tipul commenzi, o sa
cautam userul caruia ii apartine comanda, si o sa apelam functia corespunzatoare.

Programul contine urmatoarele interfete:
->Visitor care contine o metoda visit care este OVERLOADED, in functie de tipul
de user primit aceasta executa un cod diferit
->Visitable care contine metoda currentPage


Majoritatea programului se bazeaza pe urmatoarele clase:
->Player, care contine urmatoarele variabile:
	-lasttimestamp->retine ultimul timestamp la care am lucrat cu playerul
	-timeremaining->retine timpul ramas din fisierul curent
	-shuffle->retine daca este activat sau dezactivat shuffleul
	-pause->retine daca este pe pauza sau nu
	-repeat->retine modul actual al lui repeat
	-type->retine tipul sursei curente
	-source->este o clasa de tip obiect care o sa retina sursa curenta(song,
playlist sau podcast)
	-order->care este ordinul in care vor fi selectate melodiile dintr-un
playlist
	-currFile->care este o clasa in care retinem fisierul curent activ pe
player
	-currentObject->retine al catelea obiect dintr-un playlist/podcast este
currFile-ul actual

In player o sa avem urmatoarele metode:
	-status(Command command)->aceasta o sa updateze statusul curent al player-
ului in functie de timpul trecut de la lasttimestamp si timestampul comenzii 
actuale, si tine cont de shuffle si repeat, si verifica daca o sa mai ramana
o sursa activa in player
	-forward->o sa avanseze statusul playerului cu 90 de secunde
	-backward->o sa dezavanseze statusul playerului cu 90 de secunde
	-next->trece la urmatoarea melodie din playlist
	-prev->trece la melodia precedenta din playlist
	
->Userul este cea mai complexa si importanta clasa din program, ea implementeaza
interfata Visitable si contine urmatoarele variabile:
	-username->numele userului
	-age->varsta userului
	-city->orasul din care provien userul
	-lastsearch->o sa tina minte tipul obiectului cautat ultima data, initial
este setat pe "nothing", pentru ca nu a avut loc nicio cautare
	-searcheditems->este o lista cu rezultatele functie de search
	-selecteditem->este obiectul selectat dupa apelarea comenzii de select
	-player->O sa fie o Player-ul personal al userului, astfel fiecare user
are propriul lui player.
	-preferedSongs->O lista cu cantecele la care a dat like utilizatorul
	-followed->O lista cu playlisturile la care a dat follow utilizatorul
	-o sa avem clasa interna PodcasInfo care va tine minte numele unui Podcast
, numarul episodului curent, si timpul ramas din acesta cand se scoate un playlist
din player.
	-type->tipul userului curent(UserNormal, Artist, Host)
	-currentPage->tipul pagini pe care se afla userul
	-currUserrPage->Userul pagini pe care se afla userul actual
	-ultima variabila este lista podcastInfos, care este o lista care contine
clase de tipul prezentat anterior
In user avem si urmatoarele metode:
	-containsPodcast->verifica daca podcastul curent pe player exista deja in
lista descrisa mai sus
	-updatePlayer->updateaza playerul la cum ar trebui sa fie la momentul dat
de timestampul comenzii curente
	-search->o sa creeze o lista cu elementele care respecta criteriul de search
	-select->selecteaza un element din lista data de metoda de mai sus
	-load->da load la elementul selectat in playerul userului
	-playPause->da pause sau unpause la player in functie de starea actuala
a acestuia
	-status->afiseaza statusul curent al playerului
	-createPlaylist->creeaza un nou playlist
	-addRemove->adauga sau elimina cantecul care ruleaza pe player din play-
listul dat
	-repeat->schimba modul de repeat al playerului
	-shuffle->schimba modul de shuffle al playerului
	-urmatoarele 4 metode apeleaza metodele prezentate mai sus la player cu
acelasi nume
	-showPlaylists->afiseaza toate playlisturile detinute de un utilizator
	-like->da like sau unlike la cantecul care este actual pe player
	-showPrefered->returneaza numele tuturor melodiilor la care a dat like
utilizatorul
	-follow->da follow la un playlist
	-switchVisibility->schimba modul de visibilitate al unui playlist
	-implemeneteaza interfata visitable
	-switchConnection->schimba statusul conexiunii utilizatorului
	-changePage->schimba pagina pe care se afla utilizatorul
	-currentPage->implementeaza metoda curretn page din interfata Visitable,
care va afisa pagina pe care se afla userul

->Atat Artistul cat si Hostul MOSTENESC clasa User si contin variabile si functii
diferite in functie de tipul acestora, Artistul contine doua clase interne Merch
si Event, in timp ce Hostul contine clasa interna Announcement.
	
->PrintCurrentPage implementeaza interfata Visit, si se ocupa cu printarea pagini
pe care se afla un user

Programul mai contine si clasa CommandResult din care sunt MOSTENITE clasele pentru
output, clasa AudioFiles din care sunt derivate Song si Episode si clasele Podcast,
Playlis pentru a retine Podcasturile si Playlisturile, si bubblesort care face un
bubblesort.

Deoarece nu au fost aduagate multe clase noi, insa multe metode s-au schimbat sau
au avut lucruri noi adaugate o sa notez mai jos modul in care merg noile functio-
nalitati aduagate programului in etapa 3:

-Wrapper -> ideea la wrapper este destul de simpla, in user cream o clasa interna
numita Wrapper si de fiecare data cand incarcam o noua sursa in player (mai putin
un ad) o sa apelam functia UpdateWrapper care o sa updateze Wraperul userului
respectiv. Functionalitatea este aceeasi si pentru Host si Artist, doar ca acestia
au Wrapperi diferiti fata de user.

-Monetization-> O sa il impart in patru mari categori:
	-Mai intai in userbase o sa avem o clasa ArtistData care functioneaza
foarte asemanator cu un Wrapper doar ca pe cand Wrapperul este creat pentru toti
useri/artisti/hosti care se afla pe platforma, acesta clasa este creata pentru
orice artist(chiar si cei care nu sunt pe platforma) care au avut cel putin in 
fisier ascultat de un user sau de la care a cumparat cel putin un user merch
	-Merchul functioneaza foarte simplu, atunci cand un user se afla pe pagina
unui artist acesta poate cumpara merch de la artistul respectiv, si daca reuseste
o sa ii fie updatata lista de merch pe care o detine, si o sa fie updatat si
ArtistData-ul artistului respectiv
	-Premium, cand un user este premium o sa ii retinem cantecele pe care le
asculta intr-o lista si numarul de care au fost ascultate, iar cand acesta isi
da cancel la premium o sa updatam ArtistData-ul pentru toti artisti care au
cantece in acea lista. La finalul programului o sa dam cancel la premium la toti
useri
	-Reclamele sunt probabil cel mai complicat aspect al monetizationului dar
ideea lor de baza este ca in player o sa tinem minte o lista cu cantecele pe care
le-a ascultat userul nonpremium si numarul de ascultari, si o sa mai avem si doua
variabile, una este un int in care o sa tinem minte 0 daca nu urmeaza o reclama
1 daca urmeaza o reclama si 2 daca avem o reclama in player, iar cealalta este
reclama insine. Ceea ce este important este ca atunci cand incarcam o reclama
pe player o sa updatam ArtistData artistilor care trebuie platiti si apoi o sa
resetam lista cu cantece ascultate de user in player.

-Recommandations -> functineasza pe un principiu foarte simplu, atunci cand o sa
primim comanda de update recommendations o sa calculam mai intai care ar trebui
sa fie noua recomandare si apoi o sa o adaugam userului in lista de recomSong sau
recomPlaylist.

-notifications si page history sunt explicate la sectiunea de design patternuro


