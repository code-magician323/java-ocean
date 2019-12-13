## lock
### read lock 
- env: session01 have read lock, session2 no limit
- session01: 
    - [read lock table] session01 just can read lock table
    - [read others] even cannot read other tables
    - [update lock table] cannot update this table
    - [update others] cannot update operation until unlock
   
- session02:
    - [read lock table]  can read session01 locked table: `because read lock is shared`
    - [read others] can read other tables
    - [update lock table] blocked by session01 until session01 unlock table，`then finish update operation`.
    - [update others] can update others table without limit