var personApi = Vue.resource('/person{/id}')
var personWishApi = Vue.resource('/person/62/wishes{/wishId}')

function getPersonWishRecourse(personId) {
    return Vue.resource('person/' + personId + '/wishes{/wishId}')
}

Vue.component('wish-form', {
    props: ['wishes'],
    data: function () {
        return {
            text: ''
        }
    },
    template: '<div>' +
        '<input type="text" placeholder="Type your wish here" v-model="text" />' +
        '<input type="button" value="Save" @click="save"/>' +
        '</div>',
    methods: {
        save: function () {
            personWishApi.save({}, this.text).then(result =>
                result.json().then(data => {
                    this.wishes.push(data);
                    this.text = ''
                })
            )

        }
    }
});

Vue.component('wish-row', {
    props: {
        wish: Object,
        personId: Number
    },
    template: '<li><b>{{ wish.id }}</b> - {{ wish.description }} <input type="button" value="delete" @click="remove"/> </li>',
    methods: {
        remove: function () {
            getPersonWishRecourse(this.personId).remove({
                wishId: this.wish.id
            });
            this.$parent.removeWish(this.wish.id);
        }
    }
});

Vue.component('person-row', {
    props: ['person'],
    template: '<div>{{ person.lastName }} {{ person.firstName }}' +
        '<ul><wish-row v-for="wish in person.wishList" v-bind:key="wish.id" :wish="wish" :personId="person.id" /> </ul>' +
        '<wish-form :wishes="person.wishList" />' +
        '</div>',

    methods: {
        removeWish: function (index) {

            this.person.wishList = this.person.wishList.filter(function (obj) {
                return obj.id !== index;
            });
        }
    }
});

Vue.component('persons-list', {
    props: ['persons'],
    template: '<div>' +
        '<person-row v-for="person in persons" v-bind:key="person.id" :person="person" />' +
        '</div>',
    created: function () {
        personApi.get().then(result =>
            result.json().then(data =>
                data.forEach(person => this.persons.push(person))
            )
        )
    }
});


var app = new Vue({
    el: '#app',
    template: '<persons-list :persons="persons"/>',
    data: {
        persons: []
    }
})