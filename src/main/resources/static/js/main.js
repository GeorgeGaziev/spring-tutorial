var personApi = Vue.resource('/person{/id}')


Vue.component('wish-row', {
    props: ['wish'],
    template: '<li><b>{{ wish.id }}</b> - {{ wish.description }} </li>'
});

Vue.component('person-row', {
    props: ['person'],
    template:
    '<div>{{ person.lastName }} {{ person.firstName }}' +
        '<ul><wish-row v-for="wish in person.wishList" v-bind:key="wish.id" :wish="wish" /> </ul>'+
    '</div>'
});

Vue.component('persons-list', {
    props:['persons'],
    template:
    '<div>' +
        '<person-row v-for="person in persons" v-bind:key="person.id" :person="person" />'+
    '</div>',
    created: function(){
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
    persons: [
    ]
  }
})