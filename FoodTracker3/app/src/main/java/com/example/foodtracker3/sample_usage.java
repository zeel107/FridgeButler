        et_productName = findViewById(R.id.et_productName);		// EditText
        et_quantity = findViewById(R.id.et_quantity);
		
        btn_addRecord = findViewById(R.id.btn_addRecord);
        btn_requery = findViewById(R.id.btn_requery);

        // click listeners
        btn_addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper dbh = new DatabaseHelper(MainActivity.this);

                // For testing:
                Random rand = new Random();
                Date date = new Date();
                GregorianCalendar calendar = new GregorianCalendar();

                Product p = new Product();

                p.setName("Sample Product #" + rand.nextInt(1000) );        //et_productName.getText().toString() );
                p.setQuantity(rand.nextInt(100) );                          //Integer.parseInt(et_quantity.getText().toString()) );
                p.setPurchase_date(date);
                
                calendar.add(Calendar.HOUR_OF_DAY, rand.nextInt(337) );
                date = calendar.getTime();
                
                p.setExpiration_date(date);
                p.setExpired(false);
                p.setIdCategory(rand.nextInt(6) );

                if (dbh.addOne(p))
                {
                    Toast.makeText(MainActivity.this, "Record inserted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Insert failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_requery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DatabaseHelper dbh = new DatabaseHelper(MainActivity.this);

                List<Product> products = dbh.selectAll();
                StringBuffer buf = new StringBuffer();
                for (Product p: products)       buf.append(p.toString() );

                Toast.makeText(MainActivity.this, buf.toString(), Toast.LENGTH_LONG).show();
            }
        });