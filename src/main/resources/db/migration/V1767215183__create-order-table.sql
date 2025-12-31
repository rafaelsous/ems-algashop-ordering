CREATE TABLE public."order" (
  id BIGINT NOT NULL,
  customer_id uuid NOT NULL,
  status VARCHAR(255),
  total_amount NUMERIC(38, 2),
  total_items INTEGER,
  canceled_at TIMESTAMP WITH TIME ZONE,
  created_by_user_id uuid,
  last_modified_at TIMESTAMP WITH TIME ZONE,
  last_modified_by_user_id uuid,
  paid_at TIMESTAMP WITH TIME ZONE,
  payment_method VARCHAR(255),
  placed_at TIMESTAMP WITH TIME ZONE,
  ready_at TIMESTAMP WITH TIME ZONE,
  version BIGINT,
  shipping_address_city VARCHAR(255),
  shipping_address_complement VARCHAR(255),
  shipping_address_neighborhood VARCHAR(255),
  shipping_address_number VARCHAR(255),
  shipping_address_state VARCHAR(255),
  shipping_address_street VARCHAR(255),
  shipping_address_zip_code VARCHAR(255),
  shipping_cost NUMERIC(38, 2),
  shipping_expected_date date,
  shipping_recipient_document VARCHAR(255),
  shipping_recipient_first_name VARCHAR(255),
  shipping_recipient_last_name VARCHAR(255),
  shipping_recipient_phone VARCHAR(255),
  billing_address_city VARCHAR(255),
  billing_address_complement VARCHAR(255),
  billing_address_neighborhood VARCHAR(255),
  billing_address_number VARCHAR(255),
  billing_address_state VARCHAR(255),
  billing_address_street VARCHAR(255),
  billing_address_zip_code VARCHAR(255),
  billing_document VARCHAR(255),
  billing_first_name VARCHAR(255),
  billing_last_name VARCHAR(255),
  billing_email VARCHAR(255),
  billing_phone VARCHAR(255),

  PRIMARY KEY (id)
);

CREATE INDEX idx_order_customer_id ON public."order" (customer_id);

ALTER TABLE public."order"
ADD CONSTRAINT fk_order_customer_id FOREIGN key (customer_id) REFERENCES public.customer (id);

CREATE TABLE public.order_item (
  id BIGINT NOT NULL,
  order_id BIGINT NOT NULL,
  price NUMERIC(38, 2),
  product_id uuid,
  product_name VARCHAR(255),
  quantity INTEGER,
  total_amount NUMERIC(38, 2),

  PRIMARY KEY (id)
);

CREATE INDEX idx_order_item_order_id ON public.order_item (order_id);

ALTER TABLE public.order_item
ADD CONSTRAINT fk_order_item_order_id FOREIGN key (order_id) REFERENCES public."order" (id);