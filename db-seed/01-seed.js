db = db.getSiblingDB('officyna');

const adminExists = db.users.findOne({ email: 'administrator@email.com' });

if (!adminExists) {
    db.users.insertOne({
        name: 'Administrador',
        email: 'administrator@email.com',
        password: '$2a$10$nLzmKErAAIPGJqlEWBcOAOTYErAB.3qQY3M57lZNYyXkpnN2a9LNC',
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