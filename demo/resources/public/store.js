var EventEmitter = require('events').EventEmitter;
var Assign = require('object-assign');

var state = {
	sentence: "I like to walk in the woods for hours by myself.",
	replies: [],
};

var store = Assign({}, EventEmitter.prototype, {
	emitChange: function() {
		this.emit('change');
	},
	addChangeListener: function(callback) {
		this.on('change', callback);
	},
	removeChangeListener: function(callback) {
		this.removeListener('change', callback);
	},
	getState: function() {
		return state;
	},
	save: function(reply) {
		state.replies.unshift(reply);
	}
});

module.exports = store;