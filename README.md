**KARTA PROJEKTU**

**1\. Informacje ogólne**

**Tytuł projektu:  
**KasaMate - Mobilna Aplikacja do Zarządzania Budżetem Osobistym

**Akronim projektu:  
**KM

**Data utworzenia:** 19.10.2025  
**Wersja dokumentu:** 1.1

**Zespół projektowy:**

| Imię i nazwisko     | Rola w projekcie          | Zakres odpowiedzialności                                           |
|--------------------|--------------------------|------------------------------------------------------------------|
| Piotr Trochimiuk   | kierownik projektu       | Planowanie, koordynacja, raportowanie postępów, kontakt z prowadzącym |
| Sebastian Pidek    | Programista     | Implementacja kodu, opracowanie scenariuszy testowych            |
| Mateusz Sochacki   | Tester                   | Testowanie funkcjonalne aplikacji, zgłaszanie błędów, wsparcie przy implementacji i poprawkach
| Jan Płoskonka      | Dokumentalista, analityk | Redakcja dokumentacji projektowej, raporty, tworzenie przypadków użycia, diagramy UML |




**Prowadzący:** mgr Wojciech Moniuszko  
**Jednostka dydaktyczna:** _WSPA Wydział Informatyki_

**2\. Cel projektu**

Celem projektu **KasaMate** jest stworzenie funkcjonalnej i intuicyjnej **mobilnej aplikacji**, umożliwiającej użytkownikom:

- monitorowanie swoich przychodów i wydatków,
- kategoryzowanie transakcji,
- analizowanie danych w formie wykresów,
- ustawianie budżetów i celów oszczędnościowych,
- otrzymywanie powiadomień o przekroczeniu budżetu.

Aplikacja ma pomóc użytkownikom w codziennym zarządzaniu finansami i wspierać kontrolowanie wydatków.

**3\. Uzasadnienie projektu**

Współczesne warunki ekonomiczne powodują rosnącą potrzebę świadomego zarządzania finansami osobistymi.
Wiele osób nie korzysta z dedykowanych narzędzi finansowych lub używa arkuszy kalkulacyjnych, które są mało wygodne w codziennym użytkowaniu mobilnym.

Projekt KasaMate ma na celu:

- uproszczenie procesu kontroli finansów,
- zachęcenie do systematycznego śledzenia przychodów i wydatków,
- oferowanie narzędzi do analizy finansowej w zasięgu smartfona.

**4\. Zakres projektu**

**W zakresie projektu:**

- Analiza i zebranie wymagań funkcjonalnych i niefunkcjonalnych.
- Projekt aplikacji mobilnej.
- Implementacja prototypu w technologii JavaScript + HTML / Android Studio
- Przeprowadzenie testów funkcjonalnych i użytkowych.
- Opracowanie dokumentacji projektowej i prezentacja końcowa.

**Poza zakresem projektu:**

- Obsługa wielu walut.
- Aplikacja webowa / wersja desktopowa.

**5\. Wymagania**

| **Typ**           | **Opis**                                                                                       |
|------------------|------------------------------------------------------------------------------------------------|
| Funkcjonalne     | Rejestracja, logowanie, dodawanie przychodów i wydatków, kategorie, wykresy, ustawienia budżetu, powiadomienia push |
| Niefunkcjonalne  | Czas odpowiedzi: max 2 sekundy, dostępność: 99%, intuicyjny interfejs                           |
| Interfejsowe     | Natywny interfejs mobilny Android (Kotlin, Android Studio                               |
| Bezpieczeństwo   | Logowanie użytkownika przy użyciu hasła SHA256, RODO                                           |


**6\. Zespół projektowy i role**

| Rola                     | Osoba             | Odpowiedzialność                                             |
|--------------------------|-----------------|-------------------------------------------------------------|
| Kierownik projektu       | Piotr Trochimiuk | Harmonogram, raporty, koordynacja, prezentacja            |
| Programista              | Sebastian Pidek  | Programowanie, testy, raport błędów                        |
|Tester, wsparcie          |Mateusz Sochacki  |Testy funkcjonalne, raportowanie błędów, poprawki|
| Dokumentalista, analityk | Jan Płoskonka    | Dokumentacja, raporty, Wymagania, UML                       |



**7\. Zasoby i narzędzia**

| Kategoria               | Narzędzia / technologia       | Cel zastosowania                      |
|-------------------------|------------------------------|--------------------------------------|
| Zarządzanie projektem   | Discord                      | Śledzenie zadań                       |
| Repozytorium            | GitHub                       | Kontrola wersji kodu                  |
| Analiza i projektowanie | Miro                         | Diagramy UML, mapa procesu            |
| Baza danych             | MySQL?                       | Przechowywanie danych historycznych   |
| Programowanie           | Android Studio               | Implementacja aplikacji               |
| Dokumentacja            | Google Docs                  | Tworzenie dokumentacji                |
| Komunikacja             | Discord                      | Spotkania zespołowe                   |


**8\. Harmonogram realizacji (10 spotkań)**

| **Etap** | **Zakres**                                | **Czas realizacji** | **Rezultat**                          |
|----------|------------------------------------------|-------------------|--------------------------------------|
| 1        | Tworzenie zespołów, wybór tematu, karta projektu | Tydzień 1         | Karta projektu, struktura zespołu    |
| 2        | Analiza wymagań                           | Tydzień 2         | Dokument wymagań                      |
| 3        | Projekt systemu - UML                     | Tydzień 3         | Diagramy UML                          |
| 4        | Konfiguracja środowiska                   | Tydzień 4         | Repozytorium, narzędzia              |
| 5        | Dokumentacja projektowa (I etap)         | Tydzień 5         | Projekt dokumentu                     |
| 6        | Implementacja prototypu (I etap)         | Tydzień 6-7       | Pierwsza wersja aplikacji             |
| 7        | Testowanie i poprawki                     | Tydzień 8         | Raport testów                         |
| 8        | Dokumentacja końcowa i prezentacja       | Tydzień 9-10      | Prezentacja, protokołu, odbioru      |


**9\. Analiza ryzyka**

| **Nr** | **Ryzyko**                      | **Prawdopodobieństwo** | **Skutek** | **Działania zapobiegawcze**                       |
|--------|---------------------------------|-----------------------|------------|---------------------------------------------------|
| 1      | Opóźnienie w pracy zespołu       | Średnie               | Wysoki     | Spotkania tygodniowe, śledzenie postępów         |
| 2      | Konflikty w zespole              | Niskie                | Średnie    | Jasny podział ról, komunikacja na Discordzie     |
| 3      | Utrata danych                     | Niskie                | Wysoki     | Regularne kopie na GitHubie                       |
| 4      | Niedostarczenie dokumentacji      | Niskie                | Wysoki     | Wczesne rozpoczęcie redakcji przez dokumentalistę |
| 5      | Brak doświadczenia w Kotlin  | Wysoki                | Średni     | Szkolenia                                        |


**10\. Kryteria sukcesu projektu**

- Aplikacja umożliwia rejestrowanie przychodów i wydatków.
- Użytkownik może analizować dane w postaci wykresów.
- Funkcja ustalania budżetu i powiadomień działa poprawnie.
- Interfejs jest responsywny i zgodny z makietami.
- Dokumentacja zawiera wszystkie diagramy i instrukcje.
- Projekt został zrealizowany terminowo i zaprezentowany.

**11\. Rezultaty projektu**

- Prototyp mobilnej aplikacji KasaMate (Android).
- Dokumentacja projektowa i techniczna.
- Diagramy UML, makiety UI.
- Raport testowy.
- Prezentacja końcowa (PowerPoint / PDF).
- Protokół odbioru i raport zespołowy.

**12\. Akceptacja projektu**

| **Funkcja**              | **Imię i nazwisko** | **Data**     | **Podpis** |
|--------------------------|-------------------|-------------|------------|
| Kierownik projektu       | Piotr Trochimiuk  | 19.10.2025  |            |
| Programista              | Sebastian Pidek   | 19.10.2025  |            |
| Tester, wsparcie         | Mateusz Sochacki  | 19.10.2025  |            |
| Dokumentalista, analityk | Jan Płoskonka     | 19.10.2025  |            |



Kierownik projektu Piotr Trochimiuk 19.10.2025 \_**\_**\_**\_**\____

Prowadzący mgr. Wojciech Moniuszko 19.10.2025 \_**\_**\_**\_**\____

\## Uwagi końcowe:

\- Dokument powinien być przechowywany w repozytorium projektu.

\- Aktualizacja wersji dokumentu wymaga zgody kierownika projektu i prowadzącego.

\- Każdy członek zespołu ma obowiązek zapoznać się z treścią karty i ją zaakceptować.


