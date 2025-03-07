# Filmorate - сообщество для оценки и рекомендации фильмов

Java, Spring Boot, Spring MVC, SQL, H2, REST API, JUnit
# О проекте
Filmorate - веб-приложение, в котором можно ставить оценки фильмам и выбирать кино на основе оценок других пользователей.

# Что умеет Filmorate?

- выводить топ лучших фильмов по версии пользователей;
- отображать фильмы по жанрам;
- разделять фильмы по рейтингу Ассоциации кинокомпаний;
- у вас будет собственный профиль, где будут храниться все понравившиеся вам фильмы;
- вы можете добавлять в друзья других пользователей.

## ER-Диаграмма
```mermaid
erDiagram
    FILMS {
        bigint film_id PK
        text name 
        varchar(255) description
        date release_date
        int duration
        bigint film_category_id FK
        bigint film_rating FK
        bigint users_like_id FK
     }

    FILM_GENRE {
        bigint film_id PK
        bigint genre FK
    }
    
    GENRE {
        bigint genre_id PK
        varchar(10) name
    }
    
    
    FILM_RATING {
        bigint film_rating_id PK
        varchar(10) name
        varchar(255) description
    }
    USERS {
        bigint user_id PK
        varchar(50) email
        varchar(50) login
        varchar(50) name
        date birthday
    }
    FRIENDSHIP {
        bigint user_first_id FK
        bigint user_second_id FK
    }
    
    LIKES {
        bigint film_id FK
        bigint user_id FK
    }    
   

    FILM_GENRE }|--|| FILMS: film_category_id_to_film_id
    FILM_GENRE ||--|| GENRE : film_id_to_genre_id
    FILM_RATING ||--|| FILMS : film_rating_id_to_film_id
    FILMS ||--|{ LIKES : film_id_to_film_id
    LIKES }|--|| USERS : film_id_to_user_id
    USERS ||--|{ FRIENDSHIP: film_id_to_user_first_id
    
    

    
    
```
The diagram shows the relationships between the SQL tables in the application
### Command SQL FILMS
#### findAll: 
```sql
SELECT * FROM FILMS
```
#### getByID
```sql
SELECT * FROM FILMS WHERE film_id=
```
#### getByID
```sql
SELECT * FROM FILMS WHERE film_id=
```
#### getPopularFilmTo10
```sqlite-psql
SELECT name
FROM films
WHERE film_id IN (SELECT film_id
                  FROM likes
                  GROUP BY film_id
                  ORDER BY COUNT(user_id) desc
                   LIMIT 10);
```

### Command SQL USERS
#### findAll:
```sql
SELECT * FROM USERS
```
#### getByID
```sql
SELECT * FROM USERS WHERE film_id=
```
