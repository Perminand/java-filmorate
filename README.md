# java-filmorate
Template repository for Filmorate project.

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

    FILM_CATEGORY {
        bigint category_id PK
        text name
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
   

    FILM_CATEGORY }|--|| FILMS: film_category_id_to_film_id
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