CREATE TABLE public.shopping_cart (
  id uuid NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE,
  created_by_user_id uuid,
  last_modified_at TIMESTAMP WITH TIME ZONE,
  last_modified_by_user_id uuid,
  total_amount NUMERIC(38, 2),
  total_items INTEGER,
  version BIGINT,
  customer_id uuid NOT NULL,

  PRIMARY KEY (id)
);

CREATE INDEX idx_shopping_cart_customer_id ON public.shopping_cart (customer_id);

ALTER TABLE public.shopping_cart
ADD CONSTRAINT fk_shopping_cart_customer_id FOREIGN key (customer_id) REFERENCES public.customer (id);

CREATE TABLE public.shopping_cart_item (
  id uuid NOT NULL,
  available BOOLEAN,
  created_at TIMESTAMP WITH TIME ZONE,
  created_by_user_id uuid,
  last_modified_at TIMESTAMP WITH TIME ZONE,
  last_modified_by_user_id uuid,
  product_name VARCHAR(255),
  price NUMERIC(38, 2),
  product_id uuid,
  quantity INTEGER,
  total_amount NUMERIC(38, 2),
  version BIGINT,
  shopping_cart_id uuid NOT NULL,

  PRIMARY KEY (id)
);

CREATE INDEX idx_shopping_cart_item_shopping_cart_id ON public.shopping_cart_item (shopping_cart_id);

ALTER TABLE public.shopping_cart_item
ADD CONSTRAINT fk_shopping_cart_item_shopping_cart_id FOREIGN key (shopping_cart_id) REFERENCES public.shopping_cart (id);