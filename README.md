About authadmin
---------------

authadmin provides administrative APIs services for authserver. It uses spring boot starters: rest, data and hateoes.
The authserver provides authentication and authorization services. 
It implemented cached token service, token proxy service and token revoke service on oauth2 client side.

How to run
----------
### Requirments

It requires Java version 1.8+, maven 3.5+, redis 4.0+, mysql 5+ and authserver.

### Configuration

edit src/main/resources/application.properties, change following section according to your settings:

    # for oauth2 server
    #
    oauth2.server_url=http://localhost:8282
    oauth2.client_id=fff007a807304b9a8d983f5eaa095c98
    oauth2.client_secret=secret

    # for redis
    #
    spring.redis.url=redis://localhost:6379

    # for database
    #
    spring.datasource.url=jdbc:mysql://localhost/doitincloud_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true&useUnicode=true

    spring.datasource.username=dbuser
    spring.datasource.password=rdbcache

### Run

mvn clean spring-boot:run

### Test

    # command jq is required for test
    
    cd src/scripts
    
    ./tests
    
    #OR you can run individual test as followings:
    
    ./full-clients
    ./full-terms
    ./full-users
    ./password_flow
    ./read-clients
    ./read-terms
    ./read-users
    
### Samples

    #test grant_type=password to get access_token & refresh_token

    curl -sS -X POST -u 'ccf681950c2f4ce8b12fc37fd35481a6:secret' 'http://localhost:8080/oauth/v1/token' -d 'grant_type=password&username=admin@example.com&password=123&scope=read%20write%20delete'
    {
      "access_token" : "5369159f-0ef7-4452-9c36-bab2c9813074",
      "token_type" : "bearer",
      "refresh_token" : "bd36386a-0838-4ef0-b45b-d9edb5531252",
      "expires_in" : 43199,
      "scope" : "delete read write"
    }

    #list all version 1 API endpoints

    curl -sS -H 'Authorization: Bearer 8e875bf7-69ed-4355-bcc5-00248c817055' -X GET 'http://localhost:8080/v1'
    {
      "_links" : {
        "users" : {
          "href" : "http://localhost:8080/v1/users{?page,size,sort}",
          "templated" : true
        },
        "terms" : {
          "href" : "http://localhost:8080/v1/terms{?page,size,sort}",
          "templated" : true
        },
        "clients" : {
          "href" : "http://localhost:8080/v1/clients{?page,size,sort}",
          "templated" : true
        },
        "profile" : {
          "href" : "http://localhost:8080/v1/profile"
        }
      }
    }

    #list all clients from authadmin server

    curl -sS -H 'Authorization: Bearer 8e875bf7-69ed-4355-bcc5-00248c817055' -X GET 'http://localhost:8080/v1/clients'
    {
      "_embedded" : {
        "clients" : [ {
          "client_name" : "root_client",
          "contact_email" : "root_client@example.com",
          "access_token_validity_seconds" : 3600,
          "refresh_token_validity_seconds" : 7200,
          "created_at" : "2018-06-09T07:54:45.000+0000",
          "updated_at" : "2018-06-09T07:54:45.000+0000",
          "additional_information" : { },
          "scope" : [ "read", "write", "delete" ],
          "resource_ids" : [ "oauth2-resource", "oauth2-admin-api" ],
          "authorized_grant_types" : [ "client_credentials", "password" ],
          "auto_approve_scopes" : [ "read", "write", "delete" ],
          "registered_redirect_uri" : [ ],
          "authorities" : [ "ROLE_INTERNAL" ],
          "_links" : {
            "self" : {
              "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416"
            },
            "client" : {
              "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416"
            },
            "reset-secret" : {
              "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416/reset-secret"
            },
            "set-secret" : {
              "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416/set-secret"
            }
          }
        },
        ...
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/v1/clients{?page,size,sort}",
          "templated" : true
        },
        "profile" : {
          "href" : "http://localhost:8080/v1/profile/clients"
        },
        "search" : {
          "href" : "http://localhost:8080/v1/clients/search"
        }
      },
      "page" : {
        "size" : 20,
        "totalElements" : 4,
        "totalPages" : 1,
        "number" : 0
      }
    }

    #get client from authadmin server

    curl -sS -H 'Authorization: Bearer 8e875bf7-69ed-4355-bcc5-00248c817055' -X GET 'http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416'
    {
      "client_name" : "root_client",
      "contact_email" : "root_client@example.com",
      "access_token_validity_seconds" : 3600,
      "refresh_token_validity_seconds" : 7200,
      "created_at" : "2018-06-09T07:54:45.000+0000",
      "updated_at" : "2018-06-09T07:54:45.000+0000",
      "additional_information" : { },
      "scope" : [ "read", "write", "delete" ],
      "resource_ids" : [ "oauth2-resource", "oauth2-admin-api" ],
      "authorized_grant_types" : [ "client_credentials", "password" ],
      "auto_approve_scopes" : [ "read", "write", "delete" ],
      "registered_redirect_uri" : [ ],
      "authorities" : [ "ROLE_INTERNAL" ],
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416"
        },
        "client" : {
          "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416"
        },
        "reset-secret" : {
          "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416/reset-secret"
        },
        "set-secret" : {
          "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416/set-secret"
        }
      }
    }

    #use search/findByClientId to get each user from authadmin server

    curl -sS -H 'Authorization: Bearer 8e875bf7-69ed-4355-bcc5-00248c817055' -X GET 'http://localhost:8080/v1/clients/search/ids?id=04aa9802e4d145f8b2f8f3b2207b9416'
    {
      "client_name" : "root_client",
      "contact_email" : "root_client@example.com",
      "access_token_validity_seconds" : 3600,
      "refresh_token_validity_seconds" : 7200,
      "created_at" : "2018-06-09T07:54:45.000+0000",
      "updated_at" : "2018-06-09T07:54:45.000+0000",
      "additional_information" : { },
      "scope" : [ "read", "write", "delete" ],
      "resource_ids" : [ "oauth2-resource", "oauth2-admin-api" ],
      "authorized_grant_types" : [ "client_credentials", "password" ],
      "auto_approve_scopes" : [ "read", "write", "delete" ],
      "registered_redirect_uri" : [ ],
      "authorities" : [ "ROLE_INTERNAL" ],
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416"
        },
        "client" : {
          "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416"
        },
        "reset-secret" : {
          "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416/reset-secret"
        },
        "set-secret" : {
          "href" : "http://localhost:8080/v1/clients/04aa9802e4d145f8b2f8f3b2207b9416/set-secret"
        }
      }
    }
