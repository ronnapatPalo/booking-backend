INSERT INTO public.user_account (id,email,first_name,last_name,"password") VALUES
    ('e1038eb3-c3d7-480b-9ce8-18e9735ccf12','abc@def.com','firstName','lastName','password');
INSERT INTO public.room (id,size) VALUES
    ('18ced569-ba4e-4fe6-8136-2363d63448e8',5);
INSERT INTO public.booking (id,started_at,ended_at,room_id,user_id) VALUES
    ('45c5ac1f-9bba-44ca-b327-556dafb3e0ea','2022-08-04 16:39:41.982','2022-08-04 16:39:44.998','18ced569-ba4e-4fe6-8136-2363d63448e8','e1038eb3-c3d7-480b-9ce8-18e9735ccf12');