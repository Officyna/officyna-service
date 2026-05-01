db = db.getSiblingDB('officyna');

const adminExists = db.users.findOne({ email: 'admin@officyna.com' });

if (!adminExists) {
    db.users.insertOne({
        name: 'Administrador',
        email: 'admin@officyna.com',
        password: '$2a$12$h1MbmnYDyKvQajySFEs0YONMFrg0D1.Yj.yUZuyvGD045GDGQQtoq',
        userRole: 'ADMIN',
        active: true,
        createdAt: new Date(),
        updatedAt: new Date(),
        _class: 'br.com.officyna.administrative.user.domain.UserEntity'
    });
    print('Admin user created.');
} else {
    print('Admin user already exists, skipping.');
}