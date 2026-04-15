INSERT INTO Users (id, email, password, role) 
VALUES ('70125a08-9693-4873-89ec-1ecd7b0f595e', 
        'izeladaa@ucenfotec.ac.cr', 
        '$2a$10$B.YlsSMMzNUNP6sWQx.mQeiTLYS/GEPxlxx8KGJ6X06H0I4Ma.C3u', 
        'ADMIN');

INSERT INTO Gym (id, name, owner_id) 
VALUES ('368ca9f8-13f1-4b6e-9c48-4252c00a22b0', 
        'TestGym', 
        '70125a08-9693-4873-89ec-1ecd7b0f595e');
